package score;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import io.quarkus.hibernate.orm.panache.PanacheEntity;


@Entity
public class PossibleResponse extends PanacheEntity {
  public String text;
  public Long value;

  @ManyToOne
  @JoinColumn(name = "FK_SectionId")
  private Section section;

  public Section getSection() {
    return section;
  }

  public void setSection(Section section) {
    this.section = section;
  }


}
