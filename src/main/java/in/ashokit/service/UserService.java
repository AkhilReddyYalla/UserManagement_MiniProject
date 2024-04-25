
package in.ashokit.service;

import java.util.Map;

import in.ashokit.dto.LoginDto;
import in.ashokit.dto.RegesterDto;
import in.ashokit.dto.ResetPwdDto;
import in.ashokit.dto.UserDto;

public interface UserService {

	public Map<Integer, String> getCountries();
	
	public Map<Integer, String> getStates(Integer sid);
	
	public Map<Integer, String> getCities(Integer cid);
	
	public UserDto getUser(String email);
	
	public boolean regesterUser(RegesterDto regDto);
	
	public UserDto getUser(LoginDto loginDto);
	
	public boolean resetPwd(ResetPwdDto resetDto);
	
	public String getQuote();
	
	
	
}
