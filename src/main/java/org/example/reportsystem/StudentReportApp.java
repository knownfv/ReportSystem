package org.example.reportsystem;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static org.example.reportsystem.ReportUtils.saveToHistory;

public class StudentReportApp extends Application{

    TextField nameField = new TextField();
    List<TextField> scoreFields = new ArrayList<>();
    TextArea outputArea = new TextArea();
    String[] subjects = {"Math", "English", "Chemistry", "Science"};

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Student Report Generator");

        GridPane inputGrid = new GridPane();
        inputGrid.setPadding(new Insets(10));
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);

        inputGrid.add(new Label("Student Name:"), 0, 0);
        inputGrid.add(nameField, 1, 0);

        for (int i = 0; i < subjects.length; i++) {
            inputGrid.add(new Label(subjects[i] + ":"), 0, i + 1);
            TextField tf = new TextField();
            tf.setPromptText(subjects[i]);
            scoreFields.add(tf);
            inputGrid.add(tf, 1, i + 1);
        }

        Button calculateBtn = new Button("Calculate");
        Button exportBtn = new Button("Export as PDF");
        Button historyBtn = new Button("View History");
        Button findBtn = new Button("Find Report"); // New button added

        HBox buttonBox = new HBox(10, calculateBtn, exportBtn, historyBtn, findBtn);
        buttonBox.setAlignment(Pos.CENTER);

        outputArea.setEditable(false);
        outputArea.setPrefHeight(200);

        VBox root = new VBox(15, inputGrid, outputArea, buttonBox);
        root.setPadding(new Insets(15));

        calculateBtn.setOnAction(actionEvent -> calculateReport());
        exportBtn.setOnAction(actionEvent -> exportToPdf(stage));
        historyBtn.setOnAction(actionEvent -> showHistory());
        findBtn.setOnAction(actionEvent -> findStudentReport()); // Action handler for new button

        Scene scene = new Scene(root, 550, 520);
        stage.setScene(scene);
        stage.show();
    }

    private void showHistory() {
        try (Scanner scanner = new Scanner(new File("history.txt"))) {
            StringBuilder history = new StringBuilder();
            while (scanner.hasNextLine()) {
                history.append(scanner.nextLine()).append("\n");
            }
            outputArea.setText(history.toString());
        } catch (Exception e) {
            showAlert("No history found.");
        }
    }

    private void findStudentReport() {
        String nameToFind = nameField.getText().trim();
        if (nameToFind.isEmpty()) {
            showAlert("Please enter the student's name to search.");
            return;
        }

        try (Scanner scanner = new Scanner(new File("history.txt"))) {
            scanner.useDelimiter("---");
            boolean found = false;
            while (scanner.hasNext()) {
                String report = scanner.next().trim();
                if (report.startsWith("Student Name: " + nameToFind)) {
                    outputArea.setText(report);
                    found = true;
                    break;
                }
            }

            if (!found) {
                showAlert("No report found for " + nameToFind);
            }
        } catch (Exception e) {
            showAlert("Error reading history file.");
        }
    }

    private void calculateReport() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showAlert("Please enter the student's name.");
            return;
        }

        List<Integer> scores = new ArrayList<>();
        for (TextField tf : scoreFields) {
            try {
                int score = Integer.parseInt(tf.getText().trim());
                if (score < 0 || score > 100) throw new NumberFormatException();
                scores.add(score);
            } catch (NumberFormatException e) {
                showAlert("Please enter valid scores (0-100) for all subjects.");
                return;
            }
        }

        int total = scores.stream().mapToInt(Integer::intValue).sum();
        double avg = total / (double) scores.size();
        String grade = computeGrade(avg);

        StringBuilder report = new StringBuilder();
        report.append("Student Name: ").append(name).append("\n");
        for (int i = 0; i < subjects.length; i++) {
            report.append(subjects[i]).append(": ").append(scores.get(i)).append("\n");
        }
        report.append("Total: ").append(total).append("\n");
        report.append("Average: ").append(String.format("%.2f", avg)).append("\n");
        report.append("Grade: ").append(grade).append("\n");

        outputArea.setText(report.toString());
        saveToHistory(report.toString());
    }
    private String computeGrade(double avg) {
        if (avg >= 70) return "A";
        else if (avg >= 60) return "B";
        else if (avg >= 50) return "C";
        else if (avg >= 40) return "D";
        else return "F";
    }

    private void saveToHistory(String report) {
        try (FileWriter writer = new FileWriter("history.txt", true)) {
            writer.write(report + "\n---\n");
        } catch (IOException e) {
            showAlert("Failed to save report history.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void exportToPdf(Stage stage) {
        if (outputArea.getText().isEmpty()) {
            showAlert("Please calculate the report first.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();
                document.add(new Paragraph(outputArea.getText()));
                document.close();
                showAlert("PDF exported successfully.");
            } catch (DocumentException | IOException e) {
                showAlert("Error exporting PDF: " + e.getMessage());
            }
        }
    }


    
}

