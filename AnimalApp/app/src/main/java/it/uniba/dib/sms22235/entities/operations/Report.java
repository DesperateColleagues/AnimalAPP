package it.uniba.dib.sms22235.entities.operations;

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

    public Report(String reportId, String reporter) {
        this.reportId = reportId;
        this.reporter = reporter;
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

    public String getReportHelpPictureUri() {
        return reportHelpPictureUri;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public boolean isReportReady() {
        return this.lat != 0 && this.lon != 0 && !this.reportTitle.equals("") && !this.reportDescription.equals("");
    }
}
