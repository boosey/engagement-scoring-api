package score;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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


  public void addNewResponse(ItemEvaluation eval, Section section, PossibleResponse newResponse) {

    // Check is there is already a response to this section
    List<Long> possibleSectionResponseIds = section.getPossibleResponses().stream()
      .map(pr -> pr.id)
      .collect(Collectors.toList());

    List<Long> selectedReponsesIds = eval.getSelectedResponses().stream()
      .map(sr -> sr.id)
      .collect(Collectors.toList());

    Optional<Long> foundCurrentReponseOptional = selectedReponsesIds.stream()
      .filter(sr -> possibleSectionResponseIds.stream().collect(Collectors.toSet()).contains(sr))
      .findFirst();

    // Remove current response if present
    if (foundCurrentReponseOptional.isPresent()) {
      PossibleResponse currentResponse = PossibleResponse.findById(foundCurrentReponseOptional.get());
      this.selectedResponses.remove(currentResponse);
      currentResponse.removeFromEvaluation(this);
    }

    // Add new response to m2m relation
    this.selectedResponses.add(newResponse);
    newResponse.enterIntoEvaluation(this);

    return;
  }
}
