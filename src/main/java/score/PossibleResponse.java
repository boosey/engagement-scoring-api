package score;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
public class PossibleResponse extends PanacheEntityBase {
  public String text;
  public Long value;
  public Boolean active = true;

  @Id
  @GeneratedValue
  @Column(name = "possible_response_id")
  public Long id;

  @ManyToOne
  @JoinColumn(name = "FK_SectionId")
  @JsonBackReference
  private Section section;

  public void activate() {
    active = true;
  }

  public void deactivate() {
    active = false;
  }

  public void setSection(Section section) {
    this.section = section;
  }

  @ManyToMany(mappedBy = "selectedResponses", fetch = FetchType.LAZY)
  public List<ItemEvaluation> itemEvaluations = new ArrayList<>();

}
