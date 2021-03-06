package com.sfl.coolmonkey.escommons.core.component.impl;

import com.sfl.coolmonkey.escommons.core.model.document.AbstractEsDocument;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by Arthur Asatryan.
 * Date: 7/14/17
 * Time: 2:57 PM
 */
public class Person extends AbstractEsDocument {
    private String firstName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Person)) {
            return false;
        }
        final Person person = (Person) o;
        return new EqualsBuilder()
                .append(firstName, person.firstName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(firstName)
                .toHashCode();
    }
}
