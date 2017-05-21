package gki.container.domain

import groovy.transform.CompileStatic
import groovy.transform.ToString

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotNull

@CompileStatic
@ToString(includeNames=true)
@Entity
class ChatServer {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    Long id

    @NotNull
    String name

    @NotNull
    String url

    Boolean enabled = true
}
