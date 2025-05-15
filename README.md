# 배포 AWS EC2, Docker

## Swagger
![ec2배포_스웨거](https://github.com/user-attachments/assets/9ad935c6-679c-496f-925a-1351a65b245d)
- http://3.35.141.122:8080/swagger-ui/index.html

## dockerhub
![dockerhub](https://github.com/user-attachments/assets/ed4cf10d-a1a4-48b1-ae5c-39251bac5c9e)
- dockerhub에 이미지를 올려 놓았습니다.

## docker 실행 방법
1. 이미지 받기
```bash
docker pull griotold/backend-12:2.0
```

2. docker 실행
```bash
docker run --name backend-12 -p 8080:8080 griotold/backend-12:2.0
```

3. ADMIN 계정 자동 생성
- 서버가 기동되면 자동으로 ADMIN 계정이 생성됩니다.
```java
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isPresent()) {
            log.info("관리자 계정이 이미 존재합니다.");
            return;
        }

        User adminUser = User.create(
                "admin",
                passwordEncoder.encode("admin1234"),
                "administrator",
                Role.ADMIN
        );

        userRepository.save(adminUser);
        log.info("관리자 계정이 성공적으로 생성되었습니다.");
    }
}
```
# Github Actions로 push 이벤트 발생시 테스트 동작
- /.github/workflows/run-test.yml
```
name: Run Test

on:
  push:
    branches: [ develop, feature/*, fix/*, refactor/*]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          distribution: 'adopt'
          java-version: '21'
      - name: Run All Tests
        run: |
          chmod +x ./gradlew
          ./gradlew clean test
```
![깃허브액션즈_run-test](https://github.com/user-attachments/assets/f0b9d3b0-8d16-4758-afec-eab29d88d046)

# jacoco 테스트 커버리지 97% 달성 
![jacoco_테스트커버리지_97](https://github.com/user-attachments/assets/e0c3c945-4990-4ac9-ab7c-6ab98391c317)

# 테스트 전략
## Controller 테스트 전략
```
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class AuthControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;
```
- `MockMvc`를 사용하여 HTTP 요청만 Mocking 해서 테스트 합니다.

## Service 테스트 전략
```
@ActiveProfiles("test")
@Transactional
@SpringBootTest
class AuthServiceTest {

    @Autowired AuthService authService;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired JwtTokenProvider jwtTokenProvider;
```
- 스프링 환경을 전부 띄워놓고 실제 객체들의 협력 관계를 테스트합니다.
  - PG사 연동, 메일 발송과 같은 Third-party 컴포넌트는 Mocking 합니다. 
- `@Transactional` 을 통해서 테스트 수행호 rollback이 되게 하여 각 테스트 격리시킵니다.

## Repository 테스트 전략
```
@ActiveProfiles("test")
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @DisplayName("findByUsername - 저장된 사용자 존재 시 실제 객체 반환")
    @Test
    void findByUsername_existingUser_returnsUser() {
        // given
        User user = User.create("JIN HO", "12341234", "Mentos", Role.USER);
        userRepository.save(user);

        // when
        Optional<User> foundUser = userRepository.findByUsername("JIN HO");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get())
                .usingRecursiveComparison()
                .ignoringFields("id")  // ID는 자동 생성되므로 제외
                .isEqualTo(user);
    }

    @DisplayName("findByUsername - 사용자 없을 시 Optional.empty 반환")
    @Test
    void findByUsername_nonExistentUser_returnsEmpty() {
        // when
        Optional<User> foundUser = userRepository.findByUsername("nonExistentUser");

        // then
        assertThat(foundUser).isEmpty();
    }
}
```
- `@DataJPaTest` 로 데이터 접근 계층의 빈만 로드하고 테스트를 진행합니다.
- save(), findById() 와 같은 기본 제공 메서드는 이미 검증이 되었다고 판단하여 테스트를 하지 않습니다.
- 쿼리 메서드 기능, JPQL 직접 사용한 메서드, QueryDSL 로 만든 메서드는 테스트 코드를 작성합니다.

## 단위 테스트
```
@ExtendWith(MockitoExtension.class)
class PasswordValidatorTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordValidator passwordValidator;
```
- `Mockito` 라이브러리를 사용해서 단위 테스트를 작성하기도 합니다.
  - 전체 스프링 환경을 띄울 필요가 없다고 판단되거나
  - Stub을 내가 원하는대로 해주고 싶을 때 
