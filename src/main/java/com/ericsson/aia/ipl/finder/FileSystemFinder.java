/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.aia.ipl.finder;

import static org.apache.commons.io.FileUtils.listFiles;

import static com.ericsson.aia.ipl.model.Constants.JSON_SUFFIX;
import static com.ericsson.aia.ipl.model.Constants.URI_DELIMITER;
import static com.ericsson.aia.ipl.model.Constants.XML_SUFFIX;
import static com.ericsson.aia.ipl.model.UriSchemeType.LOCAL;
import static com.ericsson.aia.ipl.util.Utils.standardizeName;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.aia.ipl.exception.IntegrationPointNotFoundException;
import com.ericsson.aia.ipl.exception.InvalidIntegrationPointException;
import com.ericsson.aia.ipl.exception.NonUniqueNameException;
import com.ericsson.aia.ipl.model.IntegrationPoint;
import com.ericsson.aia.ipl.translator.IntegrationPointTranslator;
import com.ericsson.aia.ipl.translator.JsonFileTranslator;
import com.ericsson.aia.ipl.translator.XmlFileTranslator;

/**
 * This class is responsible for finding the IntegrationPoints present in a file system.
 * It will traverse directories looking for them, if needed.
 */
public class FileSystemFinder implements IntegrationPointFinder {

    private static final int FIRST_ELEMENT_INDEX = 0;
    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemFinder.class);
    private static IntegrationPointTranslator<String> jsonTranslator = new JsonFileTranslator();
    private static IntegrationPointTranslator<String> xmlTranslator = new XmlFileTranslator();

    @Override
    public IntegrationPoint find(final String uri, final String fileName) {
        LOGGER.info("Loading all IntegrationPoints files in location defined by URI '{}'.", uri);
        final File integrationPointFile = findIntegrationPointFile(uri, fileName);
        return translateToIntegrationPoint(integrationPointFile);
    }

    private File findIntegrationPointFile(final String uri, final String fileName) {
        final Collection<File> filesMatchingFileName = findFilesMatchingFileName(uri, fileName);
        validateIfOnlyOneFileWasFound(filesMatchingFileName, uri, fileName);
        final File integrationPointFile = new ArrayList<>(filesMatchingFileName).get(FIRST_ELEMENT_INDEX);
        return integrationPointFile;
    }

    private Collection<File> findFilesMatchingFileName(final String uri, final String fileName) {
        final String uriWithoutUriScheme = extractUriSchemeFromFullUri(uri);
        final File directory = new File(uriWithoutUriScheme);
        final IOFileFilter filterByFileName = defineFileNameBasedFiler(fileName);
        final Collection<File> filesMatchingFileName = listFiles(directory, filterByFileName, TrueFileFilter.INSTANCE);
        return filesMatchingFileName;
    }

    private IntegrationPoint translateToIntegrationPoint(final File integrationPointFile) {
        validateFileExtension(integrationPointFile);
        final String filePath = integrationPointFile.getAbsolutePath();
        if (isJsonFile(integrationPointFile)) {
            return jsonTranslator.translate(filePath);
        }
        return xmlTranslator.translate(filePath);
    }

    private void validateFileExtension(final File integrationPointFile) {
        final boolean isFileExtensionUnknown = !(isJsonFile(integrationPointFile) || isXmlFile(integrationPointFile));
        if (isFileExtensionUnknown) {
            throw new InvalidIntegrationPointException(
                    "The IntegrationPoint " + integrationPointFile.getName() + " is neither a JSON nor a XML file.");
        }
    }

    private boolean isXmlFile(final File integrationPointFile) {
        return standardizeName(integrationPointFile.getName()).endsWith(XML_SUFFIX);
    }

    private boolean isJsonFile(final File integrationPointFile) {
        return standardizeName(integrationPointFile.getName()).endsWith(JSON_SUFFIX);
    }

    private void validateIfOnlyOneFileWasFound(final Collection<File> filesMatchingFileName, final String uri, final String fileName) {
        if (filesMatchingFileName == null || filesMatchingFileName.isEmpty()) {
            throw new IntegrationPointNotFoundException("IntegrationPoint named '" + fileName + "' could not be found using URI '" + uri
                    + "'. Is there a corresponding XML or JSON file for this IntegrationPoint in the file system denoted by the URI?");
        }
        final boolean moreThanOneIntegrationPointFound = filesMatchingFileName.size() > 1;
        if (moreThanOneIntegrationPointFound) {
            throw new NonUniqueNameException("Multiple IntegrationPoints found while searching for '" + fileName + "', using URI '" + uri
                    + "'. The file name should be unique, regardless of directory or file extension.");
        }
    }

    private IOFileFilter defineFileNameBasedFiler(final String fileName) {
        return new IOFileFilter() {
            @Override
            public boolean accept(final File file) {
                return file.getName().startsWith(fileName);
            }

            @Override
            public boolean accept(final File dir, final String name) {
                return false;
            }
        };
    }

    private String extractUriSchemeFromFullUri(final String integrationPointLocationUri) {
        final int lengthOfUriScheme = LOCAL.getUriScheme().length() + URI_DELIMITER.length();
        return integrationPointLocationUri.substring(lengthOfUriScheme);
    }

}
