package com.mephiboys.satia.kernel.impl.entitiy;

import javax.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
    private Long roleId;
    private String role;

    @Id
    @Column(name = "role_id")
    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    @Basic
    @Column(name = "role")
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role1 = (Role) o;

        if (roleId != role1.roleId) return false;
        if (role != null ? !role.equals(role1.role) : role1.role != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = roleId == null ? 1 : (int)roleId.longValue();
        result = 31 * result + (role != null ? role.hashCode() : 0);
        return result;
    }
}
