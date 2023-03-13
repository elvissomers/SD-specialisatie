package wt.bookstore.backend.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import wt.bookstore.backend.domains.Book;
import wt.bookstore.backend.domains.Copy;
import wt.bookstore.backend.domains.Loan;
import wt.bookstore.backend.domains.User;
import wt.bookstore.backend.dto.ChangeLoanDto;
import wt.bookstore.backend.dto.LoanDto;
import wt.bookstore.backend.dto.SaveLoanDto;
import wt.bookstore.backend.dto.SaveReservationDto;
import wt.bookstore.backend.mapping.LoanDtoMapper;
import wt.bookstore.backend.repository.IBookRepository;
import wt.bookstore.backend.repository.ICopyRepository;
import wt.bookstore.backend.repository.ILoanRepository;
import wt.bookstore.backend.repository.IUserRepository;

/**
 * The controller class that sets the API endpoints for the CRUD operations of the database that handles the loans.
 */
@RestController
@CrossOrigin(maxAge = 3600)
public class LoanController {

	@Autowired
	private ILoanRepository loanRepository;

	@Autowired
	private IUserRepository userRepository;

	@Autowired
	private IBookRepository bookRepository;

	@Autowired
	private ICopyRepository copyRepository;

	@Autowired
	private LoanDtoMapper loanMapper;

	/*
	 * GET endpoints from here
   */
   
	/**
	 * Returns a Stream of {@link wt.bookstore.backend.dto.LoanDto} for a GET request to {database_location}/loan.
	 * @return Stream of {@link wt.bookstore.backend.dto.LoanDto}'s
	 */
   
	@GetMapping("loan")
	public Stream<LoanDto> findAll() {
		// Loan omzetten naar LoanDto
		return loanRepository.findAll().stream().map(loanMapper::loanToDto);
	}

	/**
	 * Returns a single {@link wt.bookstore.backend.dto.LoanDto} with a certain id for a GET request to {database_location}/loan/{id}.
	 * @param id (long) of the loan you want to get.
	 * @return Single {@link wt.bookstore.backend.dto.LoanDto}
	 */
	@GetMapping("loan/{id}")
	public Optional<LoanDto> find(@PathVariable long id) {
		return Optional.of(loanMapper.loanToDto(loanRepository.findById(id).get()));
	}


	/*
	 * POST endpoints from here
   */
   
	/**
	 * Creates a {@link wt.bookstore.backend.domains.Loan} object from a {@link wt.bookstore.backend.dto.SaveLoanDto} and saves it to the database for a POST request to {database_location}/loan/create. The id is autogenerated.
	 * @param saveLoanDto ({@link wt.bookstore.backend.dto.SaveLoanDto}) is generated from the json body in the POST request and contains the information needed to create a {@link wt.bookstore.backend.domains.Loan} object.
	 */
	@PostMapping("loan/create")
	public boolean create(@RequestBody SaveLoanDto saveLoanDto) {
		Loan loan = loanMapper.dtoToLoan(saveLoanDto);
		if (loan != null) {
			loanRepository.save(loan);
			return true;
		}
		return false;
	}


	/**
	 * Used to create a Loan from a reservation Dto object.
	 * If no copies of the reserved book are available, it gives a 500 http error
	 * @param saveReservationDto
	 */
	@PostMapping("loan/create/fromreservation")
	public boolean createFromReservation(@RequestBody SaveReservationDto saveReservationDto){
		Loan loan = new Loan();

		Optional<User> user = userRepository.findById(saveReservationDto.getUserId());
		Optional<Book> book = bookRepository.findById(saveReservationDto.getBookId());
		List<Copy> copyList = copyRepository.findByAvailableTrueAndBook(book.get());

		if (user.isEmpty() || book.isEmpty()){
			return false;
		}
		if (copyList.isEmpty()){
			return false;
		}

		Copy copy = getRandomElement(copyList);
		Optional<Copy> copyOptional = copyRepository.findById(copy.getId());
		copyOptional.get().setAvailable(false);
		copyRepository.save(copyOptional.get());

		// TODO : this should be the current date instead of the reservation start date!
		loan.setStartDate(saveReservationDto.getDate());
		loan.setCopy(copyOptional.get());
		loan.setUser(user.get());

		loanRepository.save(loan);
		return true;
	}


	/*
	 * PUT endpoints from here
	 */


	@PutMapping("loan/{id}")
	public void update(@PathVariable long id, @RequestBody ChangeLoanDto changeLoanDto){
		Optional<Loan> optionalLoan = loanRepository.findById(id);
		LocalDate newEndDate = changeLoanDto.getEndDate();
		LocalDate newStartDate = changeLoanDto.getStartDate();

		if (newEndDate != null){
			optionalLoan.get().setEndDate(newEndDate);
		}
		if (newStartDate != null){
			optionalLoan.get().setStartDate(newStartDate);
		}

		loanRepository.save(optionalLoan.get());
	}


	/*
	 * DELETE endpoints from here
	 */
	@DeleteMapping("loan/{id}")
	public void delete(@PathVariable long id) {
		loanRepository.deleteById(id);
	}


	// TODO : move "misc" functions to separate file ?
	public Copy getRandomElement(List<Copy> list){
		Random rand = new Random();

		Copy randomCopy = list.get(rand.nextInt(list.size()));

		return randomCopy;
	}

}