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

package com.tomshley.hexagonal.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TempFile extends LocalFile {
    private static final Logger log = LoggerFactory.getLogger(TempFile.class);
    private static final long serialVersionUID = 1000L;

    //~ Fields ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private ResourceFile sourceFile;

    //~ Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public TempFile(final String pathname) {
        super(pathname);
    }

    //~ Properties ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public ResourceFile getSourceFile() {
        return this.sourceFile;
    }

    public void setSourceFile(final ResourceFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    //~ Public Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public boolean flushOutput() throws IOException {
        if (this.sourceFile == null) {
            return false;
        }

        log.debug("Flushing resource from '{}' to '{}'...", this, sourceFile.toString());
        return this.sourceFile.flushOutput();
    }

}
