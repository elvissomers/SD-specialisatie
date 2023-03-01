package wt.bookstore.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import wt.bookstore.backend.domains.User;
import wt.bookstore.backend.repository.IUserRepository;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(maxAge = 3600)
public class userController {

    @Autowired
    private IUserRepository repository;

    @RequestMapping(value = "user", method = RequestMethod.GET)
    public List<User> findAll() {
        return repository.findAll();
    }

    @RequestMapping(value="user/create", method = RequestMethod.POST)
    public void create(@RequestBody User user) {
        repository.save(user);
    }

    @RequestMapping(value = "user/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable long id, @RequestBody User user) {
        Optional<User> optional = repository.findById(id);
        optional.get().setName(user.getName());
        optional.get().seteMailAddress(user.geteMailAddress());
        optional.get().setAdmin(user.getAdmin());
        repository.save(optional.get());
    }

    @RequestMapping(value = "user/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable long id) {
        repository.deleteById(id);
    }


}