package wt.bookstore.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import wt.bookstore.backend.domains.*;
import wt.bookstore.backend.dto.ChangeReservationDto;
import wt.bookstore.backend.dto.ReservationDto;
import wt.bookstore.backend.dto.SaveReservationDto;
import wt.bookstore.backend.mapping.ReservationDtoMapper;
import wt.bookstore.backend.repository.IBookRepository;
import wt.bookstore.backend.repository.ILoanRepository;
import wt.bookstore.backend.repository.IReservationRepository;
import wt.bookstore.backend.repository.IUserRepository;

import java.util.Optional;
import java.util.stream.Stream;


/**
 * The controller class that sets the API endpoints for the CRUD operations of the database that handles the reservations.
 */
@RestController
@CrossOrigin(maxAge = 3600)
public class ReservationController {

    @Autowired
    private IReservationRepository reservationRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IBookRepository bookRepository;

    @Autowired
    private ILoanRepository loanRepository;

    @Autowired
    private ReservationDtoMapper reservationMapper;


    /*
     * GET endpoints from here
     */
     
    /**
     * Returns a Stream of {@link wt.bookstore.backend.dto.ReservationDto} for a GET request to {database_location}/reservation.
     * @return Stream of {@link wt.bookstore.backend.dto.ReservationDto}'s
     */
    @GetMapping("reservation")
    public Stream<ReservationDto> findAll() {
        return reservationRepository.findAll().stream().map(reservationMapper::reservationToDto);
    }

    /**
     * Returns a single {@link wt.bookstore.backend.dto.ReservationDto} with a certain id for a GET request to {database_location}/reservation/{id}.
     * @param id (long) of the reservation you want to get.
     * @return Single {@link wt.bookstore.backend.dto.ReservationDto}
     */
    @GetMapping("reservation/{id}")
    public Optional<ReservationDto> find(@PathVariable long id) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(id);
        return Optional.of(reservationMapper.reservationToDto(optionalReservation.get()));
    }


    /*
     * POST endpoints from here
     */
     
    /**
     * Creates a {@link wt.bookstore.backend.domains.Reservation} object from a {@link wt.bookstore.backend.dto.SaveReservationDto} and saves it to the database for a POST request to {database_location}/reservation/create. The id is autogenerated.
     * @param saveReservationDto ({@link wt.bookstore.backend.dto.SaveReservationDto}) is generated from the json body in the POST request and contains the information needed to create a {@link wt.bookstore.backend.domains.Reservation} object.
     */
    @PostMapping("reservation/create")
    public boolean create(@RequestBody SaveReservationDto saveReservationDto) {
        Reservation reservation = reservationMapper.dtoToReservation(saveReservationDto);
        if (reservation != null) {
            reservationRepository.save(reservation);
            return true;
        }
        return false;
    }
    

    /*
     * PUT endpoints from here
     */

    @PutMapping("reservation/{id}/date")
    public void updateDate(@PathVariable long id, @RequestBody ChangeReservationDto changeReservationDto){
        Optional<Reservation> optionalReservation = reservationRepository.findById(id);
        optionalReservation.get().setDate(changeReservationDto.getDate());

        reservationRepository.save(optionalReservation.get());
    }


    @PutMapping("reservation/{id}")
    public boolean update(@PathVariable long id, @RequestBody SaveReservationDto saveReservationDto) {
        Optional<User> userOptional = userRepository.findById(saveReservationDto.getUserId());
        Optional<Book> bookOptional = bookRepository.findById(saveReservationDto.getBookId());
        /*
         * Converts a post DTO to a loan object, if the post DTO misses a userId, loanId
         * or reservationId it returns null, since it will not be a valid data entry
         */


        /*
         * Checks whether the id given in the url is a valid loanId
         */
        Optional<Reservation> optionalReservation = reservationRepository.findById(id);
        if (optionalReservation.isEmpty())
            return false;

        /*
         * Overwrites all the existing fields (except the ID) of the loan with the given loadId for the
         * values given in the post DTO and saves it back in the database
         */
        Reservation reservation = optionalReservation.get();

        userOptional.ifPresent(reservation::setUser);
        bookOptional.ifPresent(reservation::setBook);
        if (saveReservationDto.getDate() != null) {
            reservation.setDate(saveReservationDto.getDate());
        }

        reservationRepository.save(reservation);
        return true;
    }

    @RequestMapping(value = "reservation/{id}", method = RequestMethod.DELETE)
    public boolean delete(@PathVariable long id) {
        reservationRepository.deleteById(id);
        return true;
    }


}