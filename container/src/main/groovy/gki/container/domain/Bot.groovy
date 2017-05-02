package gki.container.domain

import groovy.transform.CompileStatic

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import java.sql.Timestamp

@CompileStatic
@Entity
class Bot {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id

    private String name
    private Timestamp createdDate
    private Timestamp updatedDate
    private boolean enabled
    private byte[] script
}
