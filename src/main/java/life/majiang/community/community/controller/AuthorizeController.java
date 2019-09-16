package life.majiang.community.community.controller;

import life.majiang.community.community.dto.AccessTokenDTO;
import life.majiang.community.community.dto.GithubUse;
import life.majiang.community.community.mapper.UserMapper;
import life.majiang.community.community.model.User;
import life.majiang.community.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthorizeController {
    @Autowired GithubProvider githubProvider;

    @Autowired
    private UserMapper userMapper;


      @Value("${github.client.id}")
      private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;

    @GetMapping("/callback")
    public String callback(@RequestParam(name="code") String code, @RequestParam(name="state")String state, HttpServletRequest request, HttpServletResponse response){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();

        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setState(state);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        System.out.println(accessToken+"这是token");
        GithubUse githubUse = githubProvider.getUser(accessToken);

        System.out.println(githubUse);
        if(githubUse!=null){
            User user=new User();


            user.setName(githubUse.getName());
            user.setAccountId(String.valueOf(githubUse.getId()));

            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setGmtCreate(System.currentTimeMillis());

            user.setGmtCreate(user.getGmtCreate());
            userMapper.insert(user);
            request.getSession().setAttribute("user",githubUse);
            /*登录成功写cookie和session*/
            response.addCookie(new Cookie("token",token));
            request.getSession().setAttribute("githubUse",githubUse);

            return  "redirect:/";
        }else{
            //登录失败，重新登录
            return  "redirect:/";
        }


    }
}
