package org.example.reportsystem;

import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import java.io.*;
public class ReportUtils {
    public static String computeGrade(double average) {
        if (average >= 70) return "A";
        else if (average >= 60) return "B";
        else if (average >= 50) return "C";
        else if (average >= 40) return "D";
        else return "F";
    }

    public static void saveToHistory(String report) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter("history.txt", true)); // 'append true' is a parameter in FileWriter constructor
            pw.println("--- Report ---\n" + report + "\n");
            pw.close(); // It's good practice to close the PrintWriter
        } catch (IOException e) {
            System.out.println("Can't save history");
        }
    }

    public static void writeToPDF(File file, String content) throws IOException {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            document.add(new Paragraph("student report card\n\n")); // Fixed the string literal
            document.add(new Paragraph(content));
        } catch (DocumentException e) {
            System.out.println("Can't write to PDF: " + e.getMessage());
        } finally {
            document.close();
        }
    }
}
