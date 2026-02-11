package wiegand.luca.ThesisIsComing.domainModel.valueObjects;

public class Link {

    private String url;
    private String anzeigetext;

    public Link(String url, String anzeigetext) {
        this.url = url;
        this.anzeigetext = anzeigetext;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAnzeigetext() {return anzeigetext;}

    public void setAnzeigetext(String anzeigetext) {this.anzeigetext = anzeigetext;}
}
