/*
 * copyright 2023 tomshley llc
 *
 * licensed under the apache license, version 2.0 (the "license");
 * you may not use this file except in compliance with the license.
 * you may obtain a copy of the license at
 *
 * http://www.apache.org/licenses/license-2.0
 *
 * unless required by applicable law or agreed to in writing, software
 * distributed under the license is distributed on an "as is" basis,
 * without warranties or conditions of any kind, either express or implied.
 * see the license for the specific language governing permissions and
 * limitations under the license.
 *
 * @author thomas schena @sgoggles <https://github.com/sgoggles> | <https://gitlab.com/sgoggles>
 *
 */

package com.tomshley.hexagonal.util;

import com.tomshley.hexagonal.format.FileFormat;
import com.tomshley.hexagonal.io.LocalFile;
import com.tomshley.hexagonal.io.ResourceFile;
import com.tomshley.hexagonal.io.TempFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class JavaFileUtils {
    private static final Logger log = LoggerFactory.getLogger(JavaFileUtils.class);

    //~ Public Constants ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @NotNull
    public static final File WORKING_DIRECTORY = determineWorkingDirectory();

    //~ Static Fields ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private static File dataDir = null;
    private static File tmpDir = null;
    private static File userDir = null;

    //~ Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private JavaFileUtils() {
    }

    //~ Static Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Nullable
    public static File cacheURIToFile(final URI uri, final String cacheDir) throws IOException {
        return cacheURLToFile(uri.toURL(), cacheDir);
    }

    @Nullable
    public static File cacheURIToLocalFile(final URI uri, final String cacheDir) throws IOException {
        return cacheURLToLocalFile(uri.toURL(), cacheDir);
    }

    @Nullable
    public static File cacheURLToFile(final URL url, final String cacheDir) throws IOException {
        final File file = getCacheFile(url, cacheDir);
        cacheUrlToCacheFile(url, file);
        return file;
    }

    @Nullable
    public static File cacheURLToLocalFile(final URL url, final String cacheDir) throws IOException {
        final File file = getLocalCacheFile(url, cacheDir);
        cacheUrlToCacheFile(url, file);
        return file;
    }

    @NotNull
    public static String convertFileSize(final long fileSize) {
        final String[] unitStrings = {"bytes", "KB", "MB", "GB", "TB"};
        final int scale = Math.max(Math.min((int) (Math.log(fileSize) / Math.log(1024)), unitStrings.length - 1), 0);

        return String.format("%,.2f %s", fileSize / Math.pow(1024, scale), unitStrings[scale]);
    }

    public static boolean copy(@NotNull final ResourceFile srcFile, @NotNull final ResourceFile destFile) {
        if (srcFile instanceof LocalFile && destFile instanceof LocalFile) {
            return copy((File) srcFile, (File) destFile);
        }

        try {
            final InputStream srcStream = srcFile.openInputStream();
            try {
                final OutputStream destStream = destFile.openOutputStream();
                try {
                    IOUtil.copy(srcStream, destStream);
                    return true;
                } finally {
                    destStream.close();
                }
            } finally {
                srcStream.close();
            }
        } catch (IOException e) {
            log.error("Unable to copy '{}' to '{}': {}", srcFile.toString(), destFile.toString(), e.getMessage());
            return false;
        }
    }

    public static boolean copy(@NotNull final File srcFile, @NotNull final File destFile) {
        if (!destFile.getParentFile().isDirectory() && !destFile.getParentFile().mkdirs()) {
            log.error("Unable to copy '{}' to '{}': Unable to create destination directory.", srcFile.getPath(), destFile.getPath());
            return false;
        }

        // We may not be required to close the channels separately from the
        // streams, but the docs are not really clear on that point.  Since
        // this is a utility method, better safe than sorry.  In any case,
        // here's an excellent example of Java syntax failings:
        try {
            final FileInputStream srcStream = new FileInputStream(srcFile);
            try {
                final FileChannel srcChannel = srcStream.getChannel();
                try {
                    final FileOutputStream dstStream = new FileOutputStream(destFile);
                    try {
                        final FileChannel dstChannel = dstStream.getChannel();
                        try {
                            dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
                            return true;
                        } finally {
                            dstChannel.close();
                        }
                    } finally {
                        dstStream.close();
                    }
                } finally {
                    srcChannel.close();
                }
            } finally {
                srcStream.close();
            }
        } catch (IOException e) {
            log.error("Unable to copy '{}' to '{}': {}", srcFile.getPath(), destFile.getPath(), e.getMessage());
            return false;
        }
    }

    public static TempFile createTempFile(final String prefix, final FileFormat format) throws IOException {
        return createTempFile(prefix, format != null ? format.getExtension() : null);
    }

    public static TempFile createTempFile(final String prefix, final String extension) throws IOException {
        final TempFile tempFile = new TempFile(File.createTempFile(prefix,
                JavaStringUtils.ensureStartsWith(extension, "."),
                getTempDirectory(true)).getAbsolutePath());
        tempFile.deleteOnExit();
        return tempFile;
    }

    public static boolean delete(@Nullable final File file) {
        if (file == null) {
            return false;
        }

        if (!file.exists() || file.delete()) {
            return true;
        }

        file.deleteOnExit();
        return false;
    }

    public static boolean ensureParentDirs(@Nullable final File file) {
        if (file == null) {
            return false;
        }

        final File parentDir = file.getParentFile();
        if (parentDir == null) {
            return false;
        }

        if (parentDir.exists()) {
            return true;
        }

        return parentDir.mkdirs();
    }

    @NotNull
    public static String extractExtension(@Nullable final String path) {
        if (StringUtils.isEmpty(path) || path.charAt(path.length() - 1) == '.') {
            return "";
        }

        // TODO: Deal with extensions like .tar.gz

        for (int i = path.length() - 2; i >= 0; i--) {
            final char curChar = path.charAt(i);
            if (curChar == '.') {
                return path.substring(i + 1).trim();
            }

            if (curChar == '/' || curChar == '\\') {
                break;
            }
        }

        return "";
    }

    @NotNull
    public static String extractFileName(@Nullable final String path) {
        if (StringUtils.isEmpty(path)) {
            return "";
        }

        int rangeStart = -1, rangeEnd = -1;

        //noinspection ConstantConditions
        final char[] pathChars = path.toCharArray();
        for (int i = pathChars.length - 1; i >= 0; i--) {
            if (rangeEnd < 0 && pathChars[i] == '.') {
                rangeEnd = i;
            }

            if (rangeStart < 0 && pathChars[i] == '/' || pathChars[i] == '\\') {
                rangeStart = i + 1;
                break;
            }
        }

        if (rangeStart < 0) {
            rangeStart = 0;
        } else if (rangeStart == pathChars.length - 1) {
            return "";
        }

        if (rangeEnd < 0) {
            rangeEnd = pathChars.length;
        }

        return path.substring(rangeStart, rangeEnd);
    }

//	@NotNull
//	public static String getContentsOfFile(@NotNull final File file) throws IOException {
//		final char[] buffer = new char[512];
//		StringBuilder builder = new StringBuilder();
//		FileReader reader = new FileReader(file);
//		BufferedReader bufferedReader = new BufferedReader(reader);
//		int len = 0;
//		try {
//			while ((len = bufferedReader.read(buffer, 0, buffer.length)) != -1) {
//				builder.append(buffer, 0, len);
//			}
//		} finally {
//			bufferedReader.close();
//		}
//		return builder.toString();
//	}

    @NotNull
    public static File getDataDirectory() {
        return getDataDirectory(true);
    }

    @NotNull
    public static File getDataDirectory(final boolean ensureExists) {
        if (dataDir != null) {
            return dataDir;
        }

        File dir = getDirectoryFromProperty("java.io.datadir", ensureExists);
        if (dir == null) {
            dir = getDirectoryFromProperty("catalina.base", ensureExists);
            if (dir == null) {
                dir = WORKING_DIRECTORY;
            }

            dir = new File(dir, "data");
            if (!dir.isDirectory() && !(dir.getParentFile().isDirectory() && (!ensureExists || dir.mkdirs()))) {
                dir = getTempDirectory(ensureExists);
            }
        }

        /*
         * Note: We don't do any synchronization here, so dataDir may be determined
         * and set multiple times at startup.  Since each thread should set the value
         * to an equivalent File object, that's OK.
         *
         * Since we fall-back if the directory cannot be created, we only set the
         * global variable if we can verify the directory exists.  This ensures that
         * we will always return the best value when the caller requests a directory
         * that exists.
         */
        if (dir.isDirectory()) {
            dataDir = dir;
        }

        return dir;
    }

    @Nullable
    public static File getDirectoryFromEnvironment(@Nullable final String environmentVariable, final boolean ensureExists) {
        final File file = getFileFromEnvironment(environmentVariable);
        if (file == null || file.isDirectory()) {
            return file;
        }

        if (file.exists()) {
            log.warn("The path '{}' defined by the environment variable '{}' exists but is not a directory.",
                    file.getAbsolutePath(), environmentVariable);
            return null;
        }

        if (ensureExists && !file.mkdirs()) {
            log.warn("Could not create directory '{}' defined by the environment variable '{}'.", file.getAbsolutePath(), environmentVariable);
            return null;
        }

        return file;
    }

    @Nullable
    public static File getDirectoryFromProperty(@Nullable final String systemProperty, final boolean ensureExists) {
        final File file = getFileFromProperty(systemProperty);
        if (file == null || file.isDirectory()) {
            return file;
        }

        if (file.exists()) {
            log.warn("The path '{}' defined by the system property '{}' exists but is not a directory.",
                    file.getAbsolutePath(), systemProperty);
            return null;
        }

        if (ensureExists && !file.mkdirs()) {
            log.warn("Could not create directory '{}' defined by the system property '{}'.", file.getAbsolutePath(), systemProperty);
            return null;
        }

        return file;
    }

    @Nullable
    public static File getFileFromEnvironment(@Nullable final String environmentVariable) {
        if (StringUtils.isEmpty(environmentVariable)) {
            return null;
        }

        try {
            final String value = System.getenv(environmentVariable);
            if (value == null) {
                return null;
            }

            return new File(value);
        } catch (SecurityException e) {
            return null;
        }
    }

    @Nullable
    public static File getFileFromProperty(@Nullable final String systemProperty) {
        if (StringUtils.isEmpty(systemProperty)) {
            return null;
        }

        try {
            final String value = System.getProperty(systemProperty);
            if (value == null) {
                return null;
            }

            return new File(value);
        } catch (SecurityException e) {
            return null;
        }
    }

    @NotNull
    public static File getTempDirectory() {
        return getTempDirectory(true);
    }

    @NotNull
    public static File getTempDirectory(final boolean ensureExists) {
        if (tmpDir != null) {
            return tmpDir;
        }

        File dir = getDirectoryFromProperty("java.io.tmpdir", ensureExists);
        if (dir == null) {
            dir = getDirectoryFromEnvironment("CATALINA_TEMP", ensureExists);
            if (dir == null) {
                dir = getDirectoryFromProperty("java.io.tmpdir", ensureExists);
                if (dir == null) {
                    dir = WORKING_DIRECTORY;
                }
            }
        }

        log.info("Temp Directory: " + dir.getAbsolutePath());

        /*
         * Note: We don't do any synchronization here, so tmpDir may be determined
         * and set multiple times at startup.  Since each thread should set the value
         * to an equivalent File object, that's OK.
         *
         * Since we fall-back if the directory cannot be created, we only set the
         * global variable if we can verify the directory exists.  This ensures that
         * we will always return the best value when the caller requests a directory
         * that exists.
         */
        if (dir.isDirectory()) {
            tmpDir = dir;
        }

        return dir;
    }

    @NotNull
    public static File getUserDirectory() {
        return getUserDirectory(true);
    }

    @NotNull
    public static File getUserDirectory(final boolean ensureExists) {
        if (userDir != null) {
            return userDir;
        }

        File dir = getDirectoryFromProperty("user.home", ensureExists);
        if (dir == null) {
            dir = WORKING_DIRECTORY;
        }

        log.info("User Directory: " + dir.getAbsolutePath());

        /*
         * Note: We don't do any synchronization here, so userDir may be determined
         * and set multiple times at startup.  Since each thread should set the value
         * to an equivalent File object, that's OK.
         *
         * Since we fall-back if the directory cannot be created, we only set the
         * global variable if we can verify the directory exists.  This ensures that
         * we will always return the best value when the caller requests a directory
         * that exists.
         */
        if (dir.isDirectory()) {
            userDir = dir;
        }

        return dir;
    }

    @Nullable
    public static List<Map<String, String>> loadCsvFile(final InputStream csvInputStream, final String[] fields) {
        try {
            final BufferedReader input = new BufferedReader(new InputStreamReader(csvInputStream));
            String inputLine = input.readLine();

            final ArrayList<Map<String, String>> wholeCsv = new ArrayList<Map<String, String>>();

            final HashMap<String, Integer> xrefMap = new HashMap<String, Integer>(fields.length);

            final String[] csvFields = inputLine.split(",");

            Integer fieldIdx = 0;
            for (final String csvField : csvFields) {
                xrefMap.put(csvField, fieldIdx++);
            }

            for (final String field : fields) {
                if (!checkXrefMap(xrefMap, field)) {
                    return null;
                }
            }

            while ((inputLine = input.readLine()) != null) {
                final HashMap<String, String> csvLine = new HashMap<String, String>(fields.length);
                final String[] inputFields = inputLine.split(",");

                for (final String field : fields) {
                    csvLine.put(field, inputFields[xrefMap.get(field)]);
                }
                wholeCsv.add(csvLine);
            }

            return wholeCsv;
        } catch (IOException e) {
            log.error("Error reading csv file: ", e);
        }
        return null;

    }

    @NotNull
    public static File newFile(@NotNull final File parent, final String... pathElements) {
        if (pathElements == null || pathElements.length == 0) {
            return parent;
        }

        if (pathElements.length == 1) {
            if (JavaStringUtils.isEmpty(pathElements[0])) {
                return parent;
            }
            return new File(parent, pathElements[0]);
        }

        // Note: While this may be the most "correct" way to do this in Java, there are
        // faster ways that will work fine in the majority of cases.  We should consider
        // changing our methodology if this turns out to be too slow.
        File intermediateFile = parent;
        for (final String curPathElement : pathElements) {
            if (!JavaStringUtils.isEmpty(curPathElement)) {
                intermediateFile = new File(intermediateFile, curPathElement);
            }
        }

        return intermediateFile;
    }

    public static boolean tryCleanDirectory(@Nullable final File directory) {
        if (directory == null || !directory.exists()) {
            return false;
        }

        try {
            FileUtils.cleanDirectory(directory);
            return true;
        } catch (IOException e) {
            log.warn("Unable to clean directory: {}", e.getMessage());
            return false;
        }
    }

    //~ Internal Static Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // true if it is copied, false if it already existed; or an IOException.
    private static boolean cacheUrlToCacheFile(final URL url, final File cacheFile) throws IOException {
        try {
            if (!cacheFile.isFile()) {
                log.trace("Downloading from {} to {}", url, cacheFile);
                final HttpURLConnection uc = (HttpURLConnection) url.openConnection();
                uc.setReadTimeout(180 * 1000); // 3 minutes should be enough
                InputStream is;
                try {
                    is = uc.getInputStream();
                } catch (IOException e) {
                    log.warn("IOException fetching url; trying error stream");
                    is = uc.getErrorStream();
                }
                final FileOutputStream fos = new FileOutputStream(cacheFile);
                int oneChar;
                if (is == null) {
                    throw new IOException("Could not open input" + url);
                }
                while ((oneChar = is.read()) != -1) {
                    fos.write(oneChar);
                }
                is.close();
                fos.close();

                log.debug("Downloaded {} to {} ", url, cacheFile);
                return true;
            }

            log.debug("Using cached file {} for {}", cacheFile, url);
            return false;
        } catch (SocketTimeoutException e) {
            log.debug("Connection timed out: {}; throwing IOException", e);
            throw new IOException(e);
        }
    }

    private static boolean checkXrefMap(final HashMap<String, Integer> xrefMap, final String fieldToCheck) {
        if (!xrefMap.containsKey(fieldToCheck)) {
            log.error("Bad format in csv file, does not contain " + fieldToCheck + " field");
            return false;
        }
        return true;
    }

    @NotNull
    private static File determineWorkingDirectory() {
        File workingDir = new File(".");
        try {
            workingDir = new File(workingDir.getCanonicalPath());
        } catch (IOException e) { /* Ignore. */ }

        log.info("Working Directory: " + workingDir.getAbsolutePath());

        return workingDir;
    }

    @NotNull
    private static File getCacheFile(@NotNull final URL url, final String cacheDir) {
        return new File(cacheDir + getCacheFileName(url));

    }

    private static String getCacheFileName(@NotNull final URL url) {

        return url.toString().replace('/', '_').
                replace('&', '_').replace(' ', '_').replace(':', '_').replace('.', '_');
    }

    @NotNull
    private static File getLocalCacheFile(@NotNull final URL url, final String cacheDir) {
        return new LocalFile(cacheDir + getCacheFileName(url));

    }

}
