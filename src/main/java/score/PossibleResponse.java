package score;

import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;


@Entity
public class PossibleResponse extends PanacheEntity {
  public String text;
  public Long value;
  public Long section;
}
