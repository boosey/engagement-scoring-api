package score;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
public class Template extends PanacheEntityBase {

  public String name;
  public Boolean active = true;

  @Id
  @GeneratedValue
  @Column(name = "section_id")
  public Long id;

  @OneToMany(mappedBy = "template", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private List<Section> sections;

  public List<Section> getSections() {
    return sections;
  }

  public List<ItemEvaluation> getEvaluations() {
    return evaluations;
  }

  @OneToMany(mappedBy = "template", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  public List<ItemEvaluation> evaluations;

  public void addSection(Section section) {
    sections.add(section);
    section.setTemplate(this);
  }

  public void addEvaluation(ItemEvaluation evaluation) {
    evaluations.add(evaluation);
    evaluation.setTemplate(this);
  }

  public void activate() {
    active = true;
  }

  public void deactivate() {
    active = false;
  }

}
