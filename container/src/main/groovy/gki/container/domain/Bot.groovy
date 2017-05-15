package gki.container.domain

import groovy.transform.CompileStatic
import groovy.transform.ToString

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotNull
import java.sql.Timestamp

@CompileStatic
@ToString(includeNames=true)
@Entity
class Bot {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    long id

    long revision = 0

    @NotNull
    String name

    @NotNull
    String createdBy

    String revisedBy

    @NotNull
    Timestamp createdDate

    Timestamp updatedDate
    boolean enabled
    boolean acceptAll

    @NotNull
    String script
}
