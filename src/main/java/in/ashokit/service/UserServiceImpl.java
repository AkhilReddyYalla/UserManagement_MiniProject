package in.ashokit.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.ashokit.dto.LoginDto;
import in.ashokit.dto.RegesterDto;
import in.ashokit.dto.ResetPwdDto;
import in.ashokit.dto.UserDto;
import in.ashokit.entity.CityEntity;
import in.ashokit.entity.CountryEntity;
import in.ashokit.entity.QuoteDto;
import in.ashokit.entity.StateEntity;
import in.ashokit.entity.UserEntity;
import in.ashokit.repo.CityRepo;
import in.ashokit.repo.CountryRepo;
import in.ashokit.repo.StateRepo;
import in.ashokit.repo.UserRepo;
import in.ashokit.utils.EmailUtils;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;
	@Autowired
	private CountryRepo countryRepo;
	@Autowired
	private StateRepo stateRepo;
	@Autowired
	private CityRepo cityRepo;
	@Autowired
	private EmailUtils emailUtils;
	
	private QuoteDto[] quotations = null;

	@Override
	public Map<Integer, String> getCountries() {
		Map<Integer, String> countryMap = new HashMap<>();

		List<CountryEntity> countryList = countryRepo.findAll();
		countryList.forEach(c -> {
			countryMap.put(c.getCountryId(), c.getCountryName());
		});

		return countryMap;
	}

	@Override
	public Map<Integer, String> getStates(Integer cid) {
		Map<Integer, String> stateMap = new HashMap<>();

		/*
		 * CountryEntity country = new CountryEntity(); country.setCountryId(cid);
		 * StateEntity state = new StateEntity(); state.setCountry(country);
		 * Example<StateEntity> of = Example.of(state); List<StateEntity> stateList =
		 * stateRepo.findAll(of);
		 */

		// by using native sql query in stateRepo
		List<StateEntity> stateList = stateRepo.findStatesByCountryId(cid);
		stateList.forEach(c -> {
			stateMap.put(c.getStateId(), c.getStateName());
		});
		return stateMap;
	}

	@Override
	public Map<Integer, String> getCities(Integer cid) {
		Map<Integer, String> cityMap = new HashMap<>();

		List<CityEntity> cityList = cityRepo.getCities(cid);
		cityList.forEach(c -> {
			cityMap.put(c.getCityId(), c.getCityName());
		});
		return cityMap;
	}

	@Override
	public UserDto getUser(String email) {
		Optional<UserEntity> userEntity = userRepo.findByEmail(email);
		UserDto dto = new UserDto();
		if (userEntity.isPresent()) {

			BeanUtils.copyProperties(userEntity.get(), dto);

			// ModelMapper mapper = new ModelMapper();
			// UserDto userDto = mapper.map(userEntity, UserDto.class);
			return dto;
		} else {
			return null;
		}
	}

	@Override
	public boolean regesterUser(RegesterDto regDto) {
		ModelMapper mapper = new ModelMapper();
		UserEntity entity = mapper.map(regDto, UserEntity.class);

		CountryEntity country = countryRepo.findById(regDto.getCountryId()).orElseThrow();
		StateEntity state = stateRepo.findById(regDto.getStateId()).orElseThrow();
		CityEntity city = cityRepo.findById(regDto.getCityId()).orElseThrow();

		entity.setCountry(country);
		entity.setState(state);
		entity.setCity(city);

		entity.setPwd(generateRandomPassword());
		entity.setPwdUpdate("NO");
		UserEntity savedEntity = userRepo.save(entity);
		String subject = "Your Regestration Successfull";
		String body = "Your Temporary Password is : " + entity.getPwd();

		emailUtils.sendEmail(regDto.getEmail(), subject, body);

		return savedEntity.getUserId() != null;
	}

	private String generateRandomPassword() {
		String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 5; i++) { // Generating password of length 5
			int randomIndex = random.nextInt(characters.length());
			sb.append(characters.charAt(randomIndex));
		}
		return sb.toString();
	}

	@Override
	public UserDto getUser(LoginDto loginDto) {
		UserEntity userEntity = userRepo.findByEmailAndPwd(loginDto.getEmail(), loginDto.getPassword());
		if (userEntity == null) {
			return null;

		}
		ModelMapper mapper = new ModelMapper();
		return mapper.map(userEntity, UserDto.class);
	}

	@Override
	public boolean resetPwd(ResetPwdDto resetDto) {
		UserEntity entity = userRepo.findByEmailAndPwd(resetDto.getEmail(), resetDto.getOldPwd());
		if (entity != null) {
			entity.setPwd(resetDto.getNewPwd());
			entity.setPwdUpdate("YES");
			userRepo.save(entity);

			return true;
		}

		return false;
	}

	@Override
	public String getQuote() {
		if(quotations==null) {
		
		String url = "https://type.fit/api/quotes";
		RestTemplate rt = new RestTemplate();
		ResponseEntity<String> forEntity = rt.getForEntity(url, String.class);
		String responseBody = forEntity.getBody();
		ObjectMapper mapper = new ObjectMapper();
		try {
			quotations = mapper.readValue(responseBody, QuoteDto[].class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
		Random r = new Random();
		int index = r.nextInt(quotations.length - 1);
		return quotations[index].getText();
	}

}
