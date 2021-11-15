package score;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
public class Section extends PanacheEntityBase {

  public String name;
  public Long weight;
  public Boolean active = true;

  @Id
  @GeneratedValue
  @Column(name = "section_id")
  public Long id;

  @OneToMany(mappedBy = "section", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<PossibleResponse> possibleResponses = new ArrayList<PossibleResponse>();

  public List<PossibleResponse> getPossibleResponses() {
    return possibleResponses;
  }

  @ManyToOne
  @JoinColumn(name = "FK_TemplateId")
  @JsonBackReference
  private Template template;

  public Template getTemplate() {
    return template;
  }

  public void setTemplate(Template t) {
    this.template = t;
  }

  public void activate() {
    active = true;
  }

  public void deactivate() {
    active = false;
  }

  public void addPossibleResponse(PossibleResponse possibleResponse) {
    possibleResponses.add(possibleResponse);
    possibleResponse.setSection(this);
  }

  // public void removePossibleResponse(PossibleResponse possibleResponse) {
  // possibleResponses.remove( possibleResponse );
  // possibleResponse.setSection( null );
  // }

}
