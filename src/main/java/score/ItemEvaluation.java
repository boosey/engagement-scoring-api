package score;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class ItemEvaluation extends PanacheEntityBase {

  public String name;
  public String description;
  public Boolean active = true;

  @Id
  @GeneratedValue
  @Column(name = "item_evaluation_id")
  public Long id;

  @ManyToOne
  @JoinColumn(name = "FK_TemplateId")
  @JsonBackReference
  private Template template;

  public Template getTemplate() {
    return template;
  }

  public void setTemplate(Template template) {
    this.template = template;
  }

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable
  public List<PossibleResponse> selectedResponses = new ArrayList<>();

}
