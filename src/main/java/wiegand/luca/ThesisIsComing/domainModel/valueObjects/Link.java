package wiegand.luca.ThesisIsComing.domainModel.valueObjects;

import java.util.Objects;
import wiegand.luca.ThesisIsComing.domainModel.exception.InvalidLinkException;

public class Link {

  private String url;
  private String anzeigetext;

  public Link(String url, String anzeigetext) {
    setUrl(url);
    setAnzeigetext(anzeigetext);
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = requireNonBlank(url, "URL darf nicht leer sein");
  }

  public String getAnzeigetext() {
    return anzeigetext;
  }

  public void setAnzeigetext(String anzeigetext) {
    this.anzeigetext = requireNonBlank(anzeigetext, "Anzeigetext darf nicht leer sein");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Link)) {
      return false;
    }
    Link other = (Link) o;
    return Objects.equals(url, other.url) && Objects.equals(anzeigetext, other.anzeigetext);
  }

  @Override
  public int hashCode() {
    return Objects.hash(url, anzeigetext);
  }

  private static String requireNonBlank(String value, String message) {
    if (value == null || value.trim().isEmpty()) {
      throw new InvalidLinkException(message);
    }
    return value.trim();
  }
}
