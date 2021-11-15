package score;

import java.util.ArrayList;
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
  public List<Section> sections = new ArrayList<Section>();

  @OneToMany(mappedBy = "template", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  public List<ItemEvaluation> evaluations = new ArrayList<>();

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

  // public void removeSection(Section section) {
  // sections.remove( section );
  // section.setTemplate( null );
  // }
}
