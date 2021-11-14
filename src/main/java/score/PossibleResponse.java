package score;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class PossibleResponse extends PanacheEntity {
  public String text;
  public Long value;
  public Boolean active = true;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "FK_SectionId")
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

}
