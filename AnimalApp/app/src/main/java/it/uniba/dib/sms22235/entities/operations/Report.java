package it.uniba.dib.sms22235.entities.operations;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class Report {
    private String reportId;
    private String reporter;
    private String reportTitle;
    private String reportDescription;
    private String reportAddress;
    private String reportAnimal;
    private String reportHelpPictureUri;

    private double lat = 0;
    private double lon = 0;

    private boolean completed;

    public Report(String reportId, String reporter) {
        this.reportId = reportId;
        this.reporter = reporter;
        completed = false;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public void setReportDescription(String reportDescription) {
        this.reportDescription = reportDescription;
    }

    public void setReportAddress(String reportAddress) {
        this.reportAddress = reportAddress;
    }

    public void setReportAnimal(String reportAnimal) {
        this.reportAnimal = reportAnimal;
    }

    public void setReportHelpPictureUri(String reportHelpPictureUri) {
        this.reportHelpPictureUri = reportHelpPictureUri;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getReportId() {
        return reportId;
    }

    public String getReporter() {
        return reporter;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public String getReportDescription() {
        return reportDescription;
    }

    public String getReportAddress() {
        return reportAddress;
    }

    public String getReportAnimal() {
        return reportAnimal;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean getCompleted() {
        return completed;
    }

    public String getReportHelpPictureUri() {
        return reportHelpPictureUri;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public boolean reportReady() {
        return this.lat != 0 && this.lon != 0 && !this.reportTitle.equals("") && !this.reportDescription.equals("");
    }

    @NonNull
    public static Report loadReport(@NonNull DocumentSnapshot document) {
        Report report = new Report(
                document.getString(KeysNamesUtils.ReportsFields.REPORT_ID),
                document.getString(KeysNamesUtils.ReportsFields.REPORTER)
        );

        report.setReportTitle(document.getString(KeysNamesUtils.ReportsFields.REPORT_TITLE));
        report.setReportDescription(document.getString(KeysNamesUtils.ReportsFields.REPORT_DESCRIPTION));
        report.setReportAddress(document.getString(KeysNamesUtils.ReportsFields.REPORT_ADDRESS));
        report.setReportAnimal(document.getString(KeysNamesUtils.ReportsFields.REPORT_ANIMAL));
        report.setCompleted(Boolean.TRUE.equals(document.getBoolean(KeysNamesUtils.ReportsFields.COMPLETED)));
        report.setLat(document.getDouble(KeysNamesUtils.ReportsFields.LAT));
        report.setLon(document.getDouble(KeysNamesUtils.ReportsFields.LON));
        report.setReportHelpPictureUri(document.getString(KeysNamesUtils.ReportsFields.REPORT_HELP_PICTURE_URI));

        return report;
    }
}
