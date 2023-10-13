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

package com.tomshley.brands.global.tech.tware.products.hexagonal.lib.format;

import com.tomshley.brands.global.tech.tware.products.hexagonal.lib.io.ResourceFile;
import com.tomshley.brands.global.tech.tware.products.hexagonal.lib.util.JavaEnumUtils;
import org.apache.commons.digester3.Digester;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FileFormat implements Comparable<FileFormat>, Serializable {
    //~ Public Constants ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @NotNull
    public static final FileFormat FALLBACK_FORMAT;
    private static final Logger log = LoggerFactory.getLogger(FileFormat.class);
    private static final long serialVersionUID = 1000L;
    //~ Static Fields ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @NotNull
    private static final String CONFIGURATION_PATH = "/META-INF/FileFormats.xml";
    @NotNull
    private static final Map<String, FileFormat> contentTypeIndex = new HashMap<String, FileFormat>();
    @NotNull
    private static final Map<String, FileFormat> extensionIndex = new HashMap<String, FileFormat>();
    @NotNull
    private static final Map<String, FileFormat> formats = new HashMap<String, FileFormat>();
    private static int maxExtensionPeriods = 0;

    //~ Initializers ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    static {
        FALLBACK_FORMAT = createFallbackFormat();
        formats.put(FALLBACK_FORMAT.getName(), FALLBACK_FORMAT);
        loadFormats();
    }

    //~ Fields ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @NotNull
    private final String name;
    @NotNull
    private final Type type;

    @Nullable
    private String contentType;
    @Nullable
    private String extension;
    @Nullable
    private String iconName;
    @Nullable
    private String title;

    //~ Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public FileFormat(@NotNull final String name, @NotNull final Type type) {
        this.name = name;
        this.type = type;
    }

    //~ Static Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static boolean equals(@Nullable final FileFormat formatA, @Nullable final FileFormat formatB) {
        return ObjectUtils.equals(formatA, formatB);
    }

    @Nullable
    public static FileFormat findByContentType(@Nullable final String contentType) {
        if (StringUtils.isEmpty(contentType)) {
            return null;
        }

        //noinspection ConstantConditions
        return contentTypeIndex.get(contentType.toLowerCase());
    }

    @Nullable
    public static FileFormat findByExtension(@Nullable final String extension) {
        if (StringUtils.isEmpty(extension)) {
            return null;
        }

        //noinspection ConstantConditions
        return extensionIndex.get(extension.toLowerCase());
    }

    @Nullable
    public static FileFormat get(@Nullable final String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }

        return formats.get(name);
    }

    @Nullable
    public static FileFormat getFormatOfFile(@Nullable final File file) {
        if (file == null) {
            return null;
        }

        return getFormatOfFile(file.getName());
    }

    @Nullable
    public static FileFormat getFormatOfFile(@Nullable final ResourceFile file) {
        if (file == null) {
            return null;
        }

        return getFormatOfFile(file.getName());
    }

    @Nullable
    public static FileFormat getFormatOfFile(@Nullable final URI uri) {
        if (uri == null) {
            return null;
        }

        final String path = uri.normalize().getPath();
        if (path == null) {
            return null;
        }

        return getFormatOfFile(path.substring(Math.max(0, path.lastIndexOf('/'))));
    }

    @Nullable
    public static FileFormat getFormatOfFile(@Nullable String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return null;
        }

        //noinspection ConstantConditions
        fileName = fileName.toLowerCase();

        Map.Entry<String, FileFormat> bestMatch = null;

        for (final Map.Entry<String, FileFormat> curExtension : extensionIndex.entrySet()) {
            //noinspection ConstantConditions
            if (!fileName.endsWith(curExtension.getKey())) {
                continue;
            }

            if (bestMatch == null) {
                bestMatch = curExtension;
                continue;
            }

            if (bestMatch.getKey().length() < curExtension.getKey().length()) {
                bestMatch = curExtension;
            }
        }

        if (bestMatch != null) {
            return bestMatch.getValue();
        }

        return null;
    }

    //~ Internal Static Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @NotNull
    private static FileFormat createFallbackFormat() {
        final FileFormat fallbackFormat = new FileFormat("Fallback", Type.OTHER);
        fallbackFormat.contentType = "application/octet-stream";
        fallbackFormat.extension = null;
        fallbackFormat.iconName = "GenericFile";
        fallbackFormat.title = "Generic File";

        return fallbackFormat;
    }

    private static int loadFormats() {
        final InputStream inputStream = FileFormat.class.getResourceAsStream(CONFIGURATION_PATH);
        if (inputStream == null) {
            log.warn("Unable to load FileFormat configuration file '{}'", CONFIGURATION_PATH);
            return -1;
        }

        final Configuration configuration = Configuration.parse(inputStream);
        if (configuration == null) {
            return 0;
        }

        int formatCount = 0;
        for (final Configuration.FormatBean curFormatSettings : configuration.getFormats()) {
            final String curFormatName = curFormatSettings.getName();
            if (StringUtils.isEmpty(curFormatName)) {
                log.error("No name provided for FileFormat.");
                continue;
            }

            //noinspection ConstantConditions
            final FileFormat curFormat = new FileFormat(curFormatName, JavaEnumUtils.tryParse(curFormatSettings.getType(), Type.OTHER));
            curFormat.iconName = curFormatSettings.getIconName();
            curFormat.title = curFormatSettings.getTitle();

            if (!curFormatSettings.getContentTypes().isEmpty()) {
                curFormat.contentType = curFormatSettings.getContentTypes().get(0);
            }

            if (!curFormatSettings.getExtensions().isEmpty()) {
                curFormat.extension = curFormatSettings.getExtensions().get(0);
            }

            formats.put(curFormat.getName(), curFormat);
            formatCount++;

            for (final String curContentType : curFormatSettings.getContentTypes()) {
                contentTypeIndex.put(curContentType.toLowerCase(), curFormat);
            }

            for (final String curExtension : curFormatSettings.getExtensions()) {
                extensionIndex.put(curExtension.toLowerCase(), curFormat);

                maxExtensionPeriods = Math.max(maxExtensionPeriods, StringUtils.countMatches(curExtension, "."));
            }
        }

        log.info("Loaded {} FileFormat(s) from '{}'", formatCount, CONFIGURATION_PATH);

        return formatCount;
    }

    //~ Properties ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Nullable
    public String getContentType() {
        return this.contentType;
    }

    @Nullable
    public String getExtension() {
        return this.extension;
    }

    @Nullable
    public String getIconName() {
        return this.iconName;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    @Nullable
    public String getTitle() {
        return this.title;
    }

    @NotNull
    public Type getType() {
        return this.type;
    }

    //~ Canonical Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public int compareTo(final FileFormat other) {
        if (other == null) {
            return this.name.compareTo(null);
        }

        return this.name.compareTo(other.name);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        final FileFormat that = (FileFormat) other;

        return this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("name", this.name).
                append("type", this.type).
                append("contentType", this.contentType).
                append("extension", this.extension).
                append("iconName", this.iconName).
                append("title", this.title).
                toString();
    }

    //~ Nested Enumerations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public enum Type {
        IMAGE, MOVIE, AUDIO, DOCUMENT, OTHER, FLASH_ENGINE, AVATAR, AVATAR_BUNDLE
    }

    //~ Inner Classes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static class Configuration {
        private static final Logger log = LoggerFactory.getLogger(Configuration.class);

        //~ Fields ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        @NotNull
        private final List<FormatBean> formats = new ArrayList<FormatBean>();

        //~ Internal Static Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        @Nullable
        static Configuration parse(@Nullable final InputStream is) {
            if (is == null) {
                return null;
            }

            final Digester digester = new Digester();
            digester.setUseContextClassLoader(true);
            digester.setValidating(false);

            // Create Top-Level Class
            digester.addObjectCreate("file-formats", "com.tomshley.brands.global.tech.tware.products.hexagonal.lib.format.FileFormat$Configuration");

            // Create Format
            digester.addObjectCreate("file-formats/format", "com.tomshley.brands.global.tech.tware.products.hexagonal.lib.format.FileFormat$Configuration$FormatBean");
            digester.addSetProperties("file-formats/format");

            // Add Content Types
            digester.addCallMethod("file-formats/format/content-types/content-type", "addContentType", 1);
            digester.addCallParam("file-formats/format/content-types/content-type", 0, "value");

            // Add Extensions
            digester.addCallMethod("file-formats/format/extensions/extension", "addExtension", 1);
            digester.addCallParam("file-formats/format/extensions/extension", 0, "value");

            // Add Format
            digester.addSetNext("file-formats/format", "addFormat", "com.tomshley.brands.global.tech.tware.products.hexagonal.lib.format.FileFormat$Configuration$FormatBean");

            try {
                return digester.parse(is);
            } catch (final SAXException e) {
                log.error("The supplied InputStream dos not contain a valid XML file.", e);

                return null;
            } catch (final IOException e) {
                log.error("An error occurred while reading the supplied InputStream.", e);

                return null;
            }
        }

        //~ Properties ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        @NotNull
        public List<FormatBean> getFormats() {
            return this.formats;
        }

        //~ Public Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        public boolean addFormat(@NotNull final FormatBean domainConfig) {
            return this.formats.add(domainConfig);
        }

        //~ Inner Classes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        public static class FormatBean {

            //~ Fields ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            @NotNull
            private final ArrayList<String> contentTypes = new ArrayList<String>();
            @NotNull
            private final ArrayList<String> extensions = new ArrayList<String>();

            @Nullable
            private String iconName;
            @Nullable
            private String name;
            @Nullable
            private String title;
            @Nullable
            private String type;

            //~ Properties ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            @NotNull
            public ArrayList<String> getContentTypes() {
                return this.contentTypes;
            }

            @NotNull
            public ArrayList<String> getExtensions() {
                return this.extensions;
            }

            @Nullable
            public String getIconName() {
                return this.iconName;
            }

            public void setIconName(@Nullable final String iconName) {
                this.iconName = iconName;
            }

            @Nullable
            public String getName() {
                return this.name;
            }

            public void setName(@Nullable final String name) {
                this.name = name;
            }

            @Nullable
            public String getTitle() {
                return this.title;
            }

            public void setTitle(@Nullable final String title) {
                this.title = title;
            }

            @Nullable
            public String getType() {
                return this.type;
            }

            public void setType(@Nullable final String type) {
                this.type = type;
            }

            //~ Public Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            public boolean addContentType(@NotNull final String contentType) {
                return this.contentTypes.add(contentType);
            }

            public boolean addExtension(@NotNull final String extensions) {
                return this.extensions.add(extensions);
            }
        }

    }
}
