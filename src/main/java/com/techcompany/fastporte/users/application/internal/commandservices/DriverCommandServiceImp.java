package com.techcompany.fastporte.users.application.internal.commandservices;

import com.techcompany.fastporte.users.domain.model.aggregates.entities.Driver;
import com.techcompany.fastporte.users.domain.model.aggregates.entities.Role;
import com.techcompany.fastporte.users.domain.model.aggregates.entities.User;
import com.techcompany.fastporte.users.domain.model.aggregates.enums.RoleName;
import com.techcompany.fastporte.users.domain.model.commands.driver.DeleteDriverCommand;
import com.techcompany.fastporte.users.domain.model.commands.driver.RegisterDriverCommand;
import com.techcompany.fastporte.users.domain.services.driver.DriverCommandService;
import com.techcompany.fastporte.users.infrastructure.persistence.jpa.DriverRepository;
import com.techcompany.fastporte.users.infrastructure.persistence.jpa.RoleRepository;
import com.techcompany.fastporte.users.infrastructure.persistence.jpa.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Transactional
@Service
public class DriverCommandServiceImp implements DriverCommandService {

    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DriverCommandServiceImp(UserRepository userRepository, DriverRepository driverRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<Driver> handle(RegisterDriverCommand command) {
        // Mapear el objeto DriverRegisterDto a un objeto Driver
        Driver driver = new Driver(command);

        // Persistir el objeto User primero
        User user = driver.getUser();

        // Recuperar el rol desde la base de datos
        Role driverRole = roleRepository.findByRoleName(RoleName.ROLE_DRIVER)
                .orElseThrow(() -> new RuntimeException("Error: El rol ROLE_DRIVER no existe en la base de datos"));

        // Asignar el rol al usuario
        user.setRoles(Set.of(driverRole));

        // Cifrar la contraseña del usuario antes de guardarlo
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(driver.getUser());

        // Asignar el objeto User persistido al objeto Driver
        driver.setUser(savedUser);
        Driver savedDriver = driverRepository.save(driver);

        //return driverMapper.driverToResponseDto(savedDriver);
        return Optional.of(savedDriver);
    }

    @Override
    public void handle(DeleteDriverCommand command) {
        driverRepository.deleteById(command.driverId());
    }
}
