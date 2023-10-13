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

package com.tomshley.brands.global.tech.tware.products.hexagonal.lib.io;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;

public class LocalFile extends File implements ResourceFile {
    private static final Logger log = LoggerFactory.getLogger(LocalFile.class);
    private static final long serialVersionUID = 1000L;

    //~ Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Creates a new {@code File} instance by converting the given
     * pathname string into an abstract pathname.  If the given string is
     * the empty string, then the result is the empty abstract pathname.
     *
     * @param pathname A pathname string
     * @throws NullPointerException If the {@code pathname} argument is {@code null}
     * @see File(String)
     */
    public LocalFile(final String pathname) {
        super(pathname);
    }

    /**
     * Creates a new {@code File} instance by converting the given
     * {@code file:} URI into an abstract pathname.
     * <p/>
     * <p> The exact form of a {@code file:} URI is system-dependent, hence
     * the transformation performed by this constructor is also
     * system-dependent.
     *
     * @param uri An absolute, hierarchical URI with a scheme equal to
     *            {@code "file"}, a non-empty path component, and undefined
     *            authority, query, and fragment components
     * @throws NullPointerException     If {@code uri} is {@code null}
     * @throws IllegalArgumentException If the preconditions on the parameter do not hold
     * @see File(URI)
     * @see URI
     * @see #toURI()
     */
    public LocalFile(final URI uri) {
        super(uri);
    }

    /**
     * Creates a new {@code File} instance from a parent pathname string
     * and a child pathname string.
     * <p/>
     * If {@code parent} is {@code null} then the new
     * {@code File} instance is created as if by invoking the
     * single-argument {@code File} constructor on the given
     * {@code child} pathname string.
     * <p/>
     * Otherwise the {@code parent} pathname string is taken to denote
     * a directory, and the {@code child} pathname string is taken to
     * denote either a directory or a file.  If the {@code child} pathname
     * string is absolute then it is converted into a relative pathname in a
     * system-dependent way.  If {@code parent} is the empty string then
     * the new {@code File} instance is created by converting
     * {@code child} into an abstract pathname and resolving the result
     * against a system-dependent default directory.  Otherwise each pathname
     * string is converted into an abstract pathname and the child abstract
     * pathname is resolved against the parent.
     *
     * @param parent The parent pathname string
     * @param child  The child pathname string
     * @throws NullPointerException If {@code child} is {@code null}
     * @see File(String,String)
     */
    public LocalFile(final String parent, final String child) {
        super(parent, child);
    }

    /**
     * Creates a new {@code File} instance from a parent abstract
     * pathname and a child pathname string.
     * <p/>
     * <p> If {@code parent} is {@code null} then the new
     * {@code File} instance is created as if by invoking the
     * single-argument {@code File} constructor on the given
     * {@code child} pathname string.
     * <p/>
     * <p> Otherwise the {@code parent} abstract pathname is taken to
     * denote a directory, and the {@code child} pathname string is taken
     * to denote either a directory or a file.  If the {@code child}
     * pathname string is absolute then it is converted into a relative
     * pathname in a system-dependent way.  If {@code parent} is the empty
     * abstract pathname then the new {@code File} instance is created by
     * converting {@code child} into an abstract pathname and resolving
     * the result against a system-dependent default directory.  Otherwise each
     * pathname string is converted into an abstract pathname and the child
     * abstract pathname is resolved against the parent.
     *
     * @param parent The parent abstract pathname
     * @param child  The child pathname string
     * @throws NullPointerException If {@code child} is {@code null}
     * @see File(File,String)
     */
    public LocalFile(final File parent, final String child) {
        super(parent, child);
    }

    //~ Properties ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Returns the time that the file was last modified.
     *
     * @return A DateTime object representing the the time this file was last modified
     * @throws SecurityException If a security manager exists and its <code>{@link
     *                           SecurityManager#checkRead(String)}</code>
     *                           method denies read access to the file
     */
    @Override
    public DateTime getLastModified() {
        return new DateTime(this.lastModified());
    }

    //~ Public Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @NotNull
    @Override
    public LocalFile asLocalFile() {
        return this;
    }

    public boolean ensureParent() {
        final File parentDir = this.getParentFile();
        if (parentDir == null || parentDir.exists()) {
            return true;
        }

        if (!parentDir.mkdirs()) {
            log.warn("Unable to create directory '{}'.", parentDir.getPath());
            return false;
        }

        return true;
    }

    @Override
    public boolean flushOutput() throws IOException {
        return true;
    }

    /**
     * Creates a {@code FileInputStream} by opening a connection to the actual file
     * named by this object in the file system. A new {@code FileDescriptor} object
     * is created to represent this file connection.
     * <p/>
     * First, if there is a security manager, its {@code checkRead} method is
     * called with the path represented by the {@code file} argument as its
     * argument.
     * <p/>
     * If the named file does not exist, is a directory rather than a regular file,
     * or for some other reason cannot be opened for reading then a
     * {@code FileNotFoundException} is thrown.
     *
     * @throws FileNotFoundException if the file does not exist,
     *                               is a directory rather than a regular file,
     *                               or for some other reason cannot be opened for
     *                               reading.
     * @throws SecurityException     if a security manager exists and its
     *                               {@code checkRead} method denies read access to the file.
     * @see File#getPath()
     * @see SecurityManager#checkRead(String)
     */
    @NotNull
    @Override
    public FileInputStream openInputStream() throws FileNotFoundException {
        return new FileInputStream(this);
    }

    /**
     * Creates a file output stream to write to the file represented by
     * this object. A new {@code FileDescriptor} object is created to
     * represent this file connection.
     * <p/>
     * First, if there is a security manager, its {@code checkWrite}
     * method is called with the path represented by the {@code file}
     * argument as its argument.
     * <p/>
     * If the file exists but is a directory rather than a regular file, does
     * not exist but cannot be created, or cannot be opened for any other
     * reason then a {@code FileNotFoundException} is thrown.
     *
     * @throws FileNotFoundException if the file exists but is a directory
     *                               rather than a regular file, does not exist but cannot
     *                               be created, or cannot be opened for any other reason
     * @throws SecurityException     if a security manager exists and its
     *                               <code>checkWrite</code> method denies write access
     *                               to the file.
     * @see File#getPath()
     * @see SecurityException
     * @see SecurityManager#checkWrite(String)
     */
    @NotNull
    @Override
    public FileOutputStream openOutputStream() throws IOException {
        if (!this.ensureParent()) {
            throw new FileNotFoundException(String.format("Could not create parent directories for file '%s'.", this.getName()));
        }

        return new FileOutputStream(this);
    }

}
