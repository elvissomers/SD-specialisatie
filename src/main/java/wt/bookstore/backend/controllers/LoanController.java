package wt.bookstore.backend.controllers;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import wt.bookstore.backend.domains.Book;
import wt.bookstore.backend.domains.Copy;
import wt.bookstore.backend.domains.Loan;
import wt.bookstore.backend.domains.User;
import wt.bookstore.backend.dto.ChangeLoanDto;
import wt.bookstore.backend.dto.LoanDto;
import wt.bookstore.backend.dto.SaveLoanDto;
import wt.bookstore.backend.dto.SaveReservationDto;
import wt.bookstore.backend.mapping.DtoMapper;
import wt.bookstore.backend.mapping.LoanDtoMapper;
import wt.bookstore.backend.repository.IBookRepository;
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
	private LoanDtoMapper loanMapper;

	/*
	 * GET endpoints from here
   */
   
	/**
	 * Returns a Stream of {@link wt.bookstore.backend.dto.LoanDto} for a GET request to {database_location}/loan.
	 * @return Stream of {@link wt.bookstore.backend.dto.LoanDto}'s
	 */
   
	@RequestMapping(value = "loan", method = RequestMethod.GET)
	public Stream<LoanDto> findAll() {
		// Loan omzetten naar LoanDto
		return loanRepository.findAll().stream().map(loanMapper::loanToDto);
	}

	/**
	 * Returns a single {@link wt.bookstore.backend.dto.LoanDto} with a certain id for a GET request to {database_location}/loan/{id}.
	 * @param id (long) of the loan you want to get.
	 * @return Single {@link wt.bookstore.backend.dto.LoanDto}
	 */
	@RequestMapping(value = "loan/{id}", method = RequestMethod.GET)
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
	@RequestMapping(value = "loan/create", method = RequestMethod.POST)
	public boolean create(@RequestBody SaveLoanDto saveLoanDto) {
		Loan loan = loanMapper.dtoToLoan(saveLoanDto);
		if (loan != null) {
			loanRepository.save(loan);
			return true;
		}
		return false;
	}


	@RequestMapping(value = "loan/create/fromreservation", method = RequestMethod.POST)
	public void createFromReservation(@RequestBody SaveReservationDto saveReservationDto){
		Loan loan = new Loan();

		Optional<User> user = userRepository.findById(saveReservationDto.getUserId());
		Optional<Book> book = bookRepository.findById(saveReservationDto.getBookId());

		Copy copy = book.get().getRandomCopy();

		loan.setStartDate(saveReservationDto.getDate());
		loan.setCopy(copy);
		loan.setUser(user.get());

		loanRepository.save(loan);
	}


	/*
	 * PUT endpoints from here
	 */
	@RequestMapping(value="loan/{id}/startdate", method = RequestMethod.PUT)
	public void updateStartDate(@PathVariable long id, @RequestBody LocalDate startDate){
		Optional<Loan> optionalLoan = loanRepository.findById(id);
		optionalLoan.get().setStartDate(startDate);

		loanRepository.save(optionalLoan.get());
	}

	@RequestMapping(value="loan/{id}/enddate", method = RequestMethod.PUT)
	public void updateEndDate(@PathVariable long id, @RequestBody LocalDate endDate){
		Optional<Loan> optionalLoan = loanRepository.findById(id);
		optionalLoan.get().setEndDate(endDate);

		loanRepository.save(optionalLoan.get());
	}

	@RequestMapping(value = "loan/{id}", method = RequestMethod.PUT)
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
	@RequestMapping(value = "loan/{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable long id) {
		loanRepository.deleteById(id);
	}



}