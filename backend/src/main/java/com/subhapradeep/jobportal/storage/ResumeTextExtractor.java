package com.subhapradeep.jobportal.storage;

import com.subhapradeep.jobportal.common.ApiException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** Pulls plain text out of a PDF so the AI can read it in step 5. */
@Component
public class ResumeTextExtractor {

    // Keeps the text sent to the AI to a sensible size
    private static final int MAX_CHARS = 20_000;

    public String extract(byte[] pdfBytes) {
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            String text = new PDFTextStripper().getText(document)
                    .replaceAll("[ \\t]+", " ")       // collapse extra spaces
                    .replaceAll("\\n{3,}", "\n\n")    // collapse blank lines
                    .trim();

            if (text.isBlank()) {
                // Scanned resumes are just images: there is no text to read
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "Could not read any text from this PDF. Please upload a text-based resume, not a scanned image.");
            }
            return text.length() > MAX_CHARS ? text.substring(0, MAX_CHARS) : text;

        } catch (IOException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "This PDF could not be opened. It may be damaged.");
        }
    }
}
