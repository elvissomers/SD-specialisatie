package wt.bookstore.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import wt.bookstore.backend.domains.Loan;
import wt.bookstore.backend.domains.Reservation;
import wt.bookstore.backend.domains.User;
import wt.bookstore.backend.dto.ChangeUserDto;
import wt.bookstore.backend.dto.SaveUserDto;
import wt.bookstore.backend.dto.UserDto;
import wt.bookstore.backend.mapping.DtoMapper;
import wt.bookstore.backend.mapping.UserDtoMapper;
import wt.bookstore.backend.repository.ILoanRepository;
import wt.bookstore.backend.repository.IReservationRepository;
import wt.bookstore.backend.repository.IUserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


/**
 * The controller class that sets the API endpoints for the CRUD operations of the database that handles the users.
 */
@RestController
@CrossOrigin(maxAge = 3600)
public class UserController {

    @Autowired
    private IUserRepository userRepository;
    
    @Autowired
    private ILoanRepository loanRepository;
    
    @Autowired
    private IReservationRepository reservationRepository;

    @Autowired
    private UserDtoMapper userMapper;


    /*
     * GET endpoints from here
     */
     
    /**
     * Returns a Stream of {@link wt.bookstore.backend.dto.UserDto} for a GET request to {database_location}/user.
     * @return Stream of {@link wt.bookstore.backend.dto.UserDto}'s
     */
    @RequestMapping(value = "user", method = RequestMethod.GET)
    public Stream<UserDto> findAll() {
        return userRepository.findAll().stream().map(userMapper::userToDto);
    }

    /**
     * Returns a single {@link wt.bookstore.backend.dto.UserDto} with a certain id for a GET request to {database_location}/user/{id}.
     * @param id (long) of the user you want to get.
     * @return Single {@link wt.bookstore.backend.dto.UserDto}
     */
    @RequestMapping(value = "user/{id}", method = RequestMethod.GET)
    public Optional<UserDto> find(@PathVariable long id) {
        return Optional.of(userMapper.userToDto(userRepository.findById(id).get()));
    }


    /*
     * POST endpoints from here
     */
     
    /**
     * Creates a {@link wt.bookstore.backend.domains.User} object from a {@link wt.bookstore.backend.dto.SaveUserDto} and saves it to the database for a POST request to {database_location}/user/create. The id is autogenerated.
     * @param saveUserDto ({@link wt.bookstore.backend.dto.SaveUserDto}) is generated from the json body in the POST request and contains the information needed to create a {@link wt.bookstore.backend.domains.User} object.
     */
    @RequestMapping(value="user/create", method = RequestMethod.POST)
    public void create(@RequestBody SaveUserDto saveUserDto) {
        User user = userMapper.dtoToUser(saveUserDto);
        userRepository.save(user);
    }


    /*
     * PUT endpoints
     */
    @RequestMapping(value = "user/{id}/firstname", method = RequestMethod.PUT)
    public void updateFirstName(@PathVariable long id, @RequestBody String firstName){
        Optional<User> optionalUser = userRepository.findById(id);
        optionalUser.get().setFirstName(firstName);

        userRepository.save(optionalUser.get());
    }

    @RequestMapping(value = "user/{id}/lastname", method = RequestMethod.PUT)
    public void updateLastName(@PathVariable long id, @RequestBody String lastName){
        Optional<User> optionalUser = userRepository.findById(id);
        optionalUser.get().setLastName(lastName);

        userRepository.save(optionalUser.get());
    }

    @RequestMapping(value = "user/{id}/emailaddress", method = RequestMethod.PUT)
    public void updateEmailAddress(@PathVariable long id, @RequestBody String emailAddress){
        Optional<User> optionalUser = userRepository.findById(id);
        optionalUser.get().seteMailAddress(emailAddress);

        userRepository.save(optionalUser.get());
    }

    @RequestMapping(value = "user/{id}/admin", method = RequestMethod.PUT)
    public void updateAdmin(@PathVariable long id, @RequestBody boolean admin){
        Optional<User> optionalUser = userRepository.findById(id);
        optionalUser.get().setAdmin(admin);

        userRepository.save(optionalUser.get());
    }

    @RequestMapping(value = "user/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable long id, @RequestBody ChangeUserDto changeUserDto){
        Optional<User> optionalUser = userRepository.findById(id);
        String newFirstName = changeUserDto.getFirstName();
        String newLastName = changeUserDto.getLastName();
        String newEmailAddress = changeUserDto.getEmailAddress();

        if (newFirstName != null){
            optionalUser.get().setFirstName(newFirstName);
        }
        if (newLastName != null){
            optionalUser.get().setLastName(newLastName);
        }
        if (newEmailAddress != null){
            optionalUser.get().seteMailAddress(newEmailAddress);
        }
        // Removed admin from changeuserdto because a boolean cannot be null

        userRepository.save(optionalUser.get());
    }
    
    /*
     * Uit gecomment omdat we put pas later gaan implementeren
     */

//    @RequestMapping(value = "user/{id}", method = RequestMethod.PUT)
//    public void update(@PathVariable long id, @RequestBody SaveUserDto saveUserDto) {
//        Optional<User> optional = userRepository.findById(id);
//        optional.get().setName(saveUserDto.getName());
//        optional.get().seteMailAddress(saveUserDto.geteMailAddress());
//        userRepository.save(optional.get());
//    }

    @RequestMapping(value = "user/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable long id) {
        userRepository.deleteById(id);
    }
    
    @RequestMapping(value = "user/{id}/loans", method = RequestMethod.GET)
    public List<Loan> findLoans(@PathVariable long id){
    	/**
    	 * Used to find all loans of a user
    	 */
    	return loanRepository.findByUserId(id);
    }
    
    @RequestMapping(value = "user/{id}/reservations", method = RequestMethod.GET)
    public List<Reservation> findReservations(@PathVariable long id){
    	/**
    	 * Used to find all reservations of a user
    	 */
    	return reservationRepository.findByUserId(id);
    }

}