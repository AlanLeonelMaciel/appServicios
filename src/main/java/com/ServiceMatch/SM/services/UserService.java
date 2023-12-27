package com.ServiceMatch.SM.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import com.ServiceMatch.SM.entities.AppUser;
import com.ServiceMatch.SM.entities.Image;
import com.ServiceMatch.SM.entities.Provider;
import com.ServiceMatch.SM.entities.Skill;
import com.ServiceMatch.SM.enums.RolEnum;
import com.ServiceMatch.SM.exceptions.MyException;
import com.ServiceMatch.SM.repository.ProviderRepository;
import com.ServiceMatch.SM.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private ServiceImage serviceImage;

    @Transactional
    public void registrar(String name, String email, String password, String password2) throws MyException {
        validar(name, email, password, password2);
        AppUser appUser = new AppUser();
        appUser.setName(name);
        appUser.setEmail(email);
        appUser.setPassword(new BCryptPasswordEncoder().encode(password));
        appUser.setRol(RolEnum.USUARIO);
        userRepository.save(appUser);
    }

    // método para editar el perfil del cliente
    @Transactional
    public void editClient(Long id, String name, String password, String password2) throws MyException {
        Optional<AppUser> result = userRepository.findById(id);
        AppUser client = new AppUser();
        AppUser c2 = new AppUser();
        if (result.isPresent()) {
            validarEdit(name);
            if (!password2.equals(password)) {
                throw new MyException("los passwords ingresados no coinciden");
            }
            client = result.get();
            c2 = result.get();
            client.setName(name);
            if (!password.isEmpty()) {
                client.setPassword(new BCryptPasswordEncoder().encode(password));
            } else {
                client.setPassword(c2.getPassword());
            }

            client.setRol(RolEnum.USUARIO);
            userRepository.save(client);

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", client);
        }

    }

    // método para cambiar de rol de cliente a proveedor ARREGLAR
    @Transactional
    public void clientToProvider(Long id, String name, String password, String password2, Long whatsApp,
            List<Skill> skills, MultipartFile file) throws MyException {
        Optional<AppUser> result = userRepository.findById(id);
        Provider provider = new Provider();
        if (result.isPresent()) {
            provider = (Provider) result.get();
            provider.setId(id);
            provider.setName(name);

            provider.setRol(RolEnum.PROVEEDOR);
            provider.setSkills(skills);
            provider.setWhatsApp(whatsApp);
            if (!file.isEmpty()) {
                Image img = serviceImage.guardarImagen(file);
                provider.setImagen(img);
            } else {
                provider.setImagen(provider.getImagen());
            }
            providerRepository.save(provider);
        }
    }

    // método para editar el perfil del proveedor
    @Transactional
    public void editProvider(Long id, String name, String password, String password2, Long whatsapp, List<Skill> skills,
            MultipartFile file) throws MyException {
        Optional<Provider> result = providerRepository.findById(id);

        validarEditProvider(name, password, password2, whatsapp, skills);

        Provider provider = new Provider();
        AppUser c2 = new AppUser();
        if (result.isPresent()) {
            provider = result.get();
            c2 = result.get();
            provider.setName(name);
            if (!password.isEmpty()) {
                provider.setPassword(new BCryptPasswordEncoder().encode(password));
            } else {
                provider.setPassword(c2.getPassword());
            }
            provider.setRol(RolEnum.PROVEEDOR);
            provider.setSkills(skills);
            if (!file.isEmpty()) {
                Image img = serviceImage.guardarImagen(file);
                provider.setImagen(img);
            } else {
                provider.setImagen(provider.getImagen());
            }
            provider.setWhatsApp(whatsapp);
            providerRepository.save(provider);
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", provider);
        }
    }

    public List<AppUser> getUsers() {
        List<AppUser> users = new ArrayList<>();
        users = userRepository.findAll();
        return users;
    }

    @Transactional
    public void deleteUser(Long id) {
        Optional<AppUser> result = userRepository.findById(id);
        AppUser user = new AppUser();
        if (result.isPresent()) {
            user = result.get();
            userRepository.delete(user);
        }
    }

    @Transactional
    public void restoreUser(Long id, boolean active) throws MyException {

        Optional<AppUser> result = userRepository.findById(id);
        AppUser user = new AppUser();
        if (result.isPresent()) {
            user = result.get();
            user.setActive(active);
            userRepository.save(user);
        }
    }

    @Transactional
    public void modifyUser(Long id, String name, String password, String mail, Long whatsApp) throws MyException {
        validar(name, mail, password, password);
        Optional<AppUser> result = userRepository.findById(id);
        AppUser user = new AppUser();
        if (result.isPresent()) {
            user = result.get();
            user.setName(name);
            user.setPassword(password);
            user.setEmail(mail);
            userRepository.save(user);
        }
    }

    @Transactional
    public void updateUser(Long id, String name, boolean active) throws MyException {
        Optional<AppUser> result = userRepository.findById(id);
        AppUser user = new AppUser();
        if (result.isPresent()) {
            user = result.get();
            user.setName(name);
            user.setActive(active);
            userRepository.save(user);
        }
    }

    public Page<AppUser> getPageOfUsers(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser appUser = userRepository.findByEmail(email);
        if (appUser != null) {
            List<GrantedAuthority> permissions = new ArrayList<>();
            GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + appUser.getRol().toString());
            permissions.add(p);
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", appUser);
            return new User(appUser.getEmail(), appUser.getPassword(), permissions);

        } else {
            return null;
        }
    }

    public AppUser getOne(Long id) {
        return userRepository.findById(id).get();
    }

    public List<AppUser> loadUserByRol(RolEnum rol) {
        return userRepository.findByRol(rol);
    }

    public List<AppUser> loadUserBySkyll(String skill) {

        return userRepository.findProvidersBySkill(skill);
    }

    private void validar(String name, String email, String password, String password2)
            throws MyException {

        if (name == null || name.isEmpty()) {
            throw new MyException("El nombre no puede ser nulo o estar vacio");
        }

        if (email == null || email.isEmpty()) {
            throw new MyException("El email no puede ser nulo o estar vacio");

        }

        if (password == null || password.isEmpty() || password.length() <= 5) {
            throw new MyException("La contraseña no puede estar vacia, y debe tener mas de 5 digitos");
        }

        if (!password2.equals(password)) {
            throw new MyException("Las contraseñas ingresadas no coinciden");

        }

    }

    private void validarEdit(String name) throws MyException {
        if (name == null || name.isEmpty()) {
            throw new MyException("El nombre no puede ser nulo o estar vacio");
        }
    }

    private void validarEditProvider(String name, String password, String password2, Long whatsapp, List<Skill> skills)
            throws MyException {
        if (name == null || name.isEmpty()) {
            throw new MyException("El nombre no puede ser nulo o estar vacio");
        }

        if (!password.equals(password2) && !password.isEmpty() && !password2.isEmpty()) {
            throw new MyException("las contraseñas deben coincidir");
        }
    }

}
