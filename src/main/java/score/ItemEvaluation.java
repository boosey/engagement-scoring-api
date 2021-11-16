package score;

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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
  @JsonIgnoreProperties("itemEvaluations")
  private List<PossibleResponse> selectedResponses;

  public List<PossibleResponse> getSelectedResponses() {
    return selectedResponses;
  }

  public void setSelectedResponses(List<PossibleResponse> selectedResponses) {
    this.selectedResponses = selectedResponses;
  }

  public void addResponse(PossibleResponse response) {
    this.selectedResponses.add(response);
    response.enterIntoEvaluation(this);
  }

  public void removeResponse(Long rid) {

    PossibleResponse response = PossibleResponse.findById(rid);

    this.selectedResponses.remove(response);
    response.removeFromEvaluation(this);
  }

}
