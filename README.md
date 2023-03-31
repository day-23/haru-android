# haru

## Commit Convention

1. `feature`: **새로운 기능 추가**
2. `fix`: **버그 수정**
3. `docs`: **문서 수정**
4. `style`: **코드 포맷팅 → Code Convention**
5. `refactor`: **코드 리팩토링**
6. `test`: **테스트 코드**
7. `chore`: **빌드 업무 수정, 패키지 매니저 수정**
8. `comment`: **주석 추가 및 수정**

커밋할 때 헤더에 위 내용을 작성하고 전반적인 내용을 간단하게 작성합니다.

### 예시

> `git commit -m "feature: 하루"`

커밋할 때 상세 내용을 작성해야 한다면 아래와 같이 진행합니다.

### 예시

> `git commit`  
> 어떠한 에디터로 진입하게 된 후 아래와 같이 작성합니다.  
> `[header]: 전반적인 내용`  
> . **(한 줄 비워야 함)**  
> `상세 내용`

## Branch Naming Convention

브랜치를 새롭게 만들 때, 브랜치 이름은 항상 위 `Commit Convention`의 Header와 함께 작성되어야 합니다.

### 예시

> `feature/haru`  
> `refactor/haru`

---
