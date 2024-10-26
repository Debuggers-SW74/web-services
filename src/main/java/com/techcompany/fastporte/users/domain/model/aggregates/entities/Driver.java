package com.techcompany.fastporte.users.domain.model.aggregates.entities;

import com.techcompany.fastporte.users.domain.model.commands.driver.RegisterDriverCommand;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Data
@Table(name = "driver")
@AllArgsConstructor
@NoArgsConstructor
public class Driver implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plate")
    private String plate;

    @OneToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    private Supervisor supervisor;

    public Driver(RegisterDriverCommand command) {

        this.user = new User(command);
        //this.plate = command.plate();
    }
}
