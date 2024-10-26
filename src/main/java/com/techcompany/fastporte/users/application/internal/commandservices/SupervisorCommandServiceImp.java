package com.techcompany.fastporte.users.application.internal.commandservices;

import com.techcompany.fastporte.users.domain.model.aggregates.entities.Role;
import com.techcompany.fastporte.users.domain.model.aggregates.entities.Supervisor;
import com.techcompany.fastporte.users.domain.model.aggregates.entities.User;
import com.techcompany.fastporte.users.domain.model.aggregates.enums.RoleName;
import com.techcompany.fastporte.users.domain.model.commands.supervisor.DeleteSupervisorCommand;
import com.techcompany.fastporte.users.domain.model.commands.supervisor.RegisterSupervisorCommand;
import com.techcompany.fastporte.users.domain.services.supervisor.SupervisorCommandService;
import com.techcompany.fastporte.users.infrastructure.persistence.jpa.RoleRepository;
import com.techcompany.fastporte.users.infrastructure.persistence.jpa.SupervisorRepository;
import com.techcompany.fastporte.users.infrastructure.persistence.jpa.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Transactional
@Service
public class SupervisorCommandServiceImp implements SupervisorCommandService {

    private final UserRepository userRepository;
    private final SupervisorRepository supervisorRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public SupervisorCommandServiceImp(UserRepository userRepository, SupervisorRepository supervisorRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.supervisorRepository = supervisorRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<Supervisor> handle(RegisterSupervisorCommand command) {
        // Mapear el objeto SupervisorRegisterDto a un objeto Supervisor
        Supervisor supervisor = new Supervisor(command);

        // Persistir el objeto User primero
        User user = supervisor.getUser();

        // Recuperar el rol desde la base de datos
        Role supervisorRole = roleRepository.findByRoleName(RoleName.ROLE_SUPERVISOR)
                .orElseThrow(() -> new RuntimeException("Error: El rol ROLE_SUPERVISOR no existe en la base de datos"));

        // Asignar el rol al usuario
        user.setRoles(Set.of(supervisorRole));

        // Cifrar la contraseña del usuario antes de guardarlo
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(supervisor.getUser());

        // Asignar el objeto User persistido al objeto Supervisor
        supervisor.setUser(savedUser);
        Supervisor savedSupervisor = supervisorRepository.save(supervisor);

        //return supervisorMapper.supervisorToResponseDto(savedSupervisor);
        return Optional.of(savedSupervisor);
    }

    @Override
    public void handle(DeleteSupervisorCommand command) {
        supervisorRepository.deleteById(command.supervisorId());
    }
}
