package mmbot.container.domain

import groovy.transform.CompileStatic
import groovy.transform.ToString

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Version
import javax.validation.constraints.NotNull
import java.sql.Timestamp

@CompileStatic
@ToString(includeNames=true)
@Entity
class Bot {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    long id

    String revision

    @NotNull
    String name

    String createdBy

    String revisedBy

    @NotNull
    Timestamp createdDate

    Timestamp updatedDate

    boolean enabled

    @Version
    long version
}
