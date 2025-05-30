### Elasticsearch 소개

**정의 : 검색, 데이터 분석에 최적화된 데이터베이스** 

**활용 사례 :** 

1. 데이터 수집 및 분석 : 대규모 데이터(ex. 로그 등)을 수집 및 분석하는데 최적화 할 수 있는 기능을 지님 
2. 검색 최적화 : 데이터가 많더라도 뛰어난 검색 속도를 가지고 있고, 오타나 동의어를 고려해서 유연하게 검색할 수 있는 기능을 지님 

**통신 방식 :** 

Elasticsearch와 소통하려면 **REST API** 방식으로 통신을 진행한다. 

ex)

```jsx
curl -X POST "localhost:9200/users/_doc" -H 'Content-Type: application/json' -d'
{
  "name": "Alice",
  "email": "alice@example.com"
}'
```

---

### **Elasticsearch의 기본 용어 정리**

**Elasticsearch도 데이터베이스다 !!**

Elasticsearch에 데이터를 **저장/조회/수정/삭제**할 수 있다. 

데이터를 저장하기 위해 가장 먼저 **인덱스(index)**를 만든다. 

인덱스를 만들 때 어떤 유형의 데이터를 넣을 지 **매핑(mapping)**을 정의한다. 

테이블의 **필드(field)**에 맞게 데이터를 저장한다. 

| MySQL | Elasticsearch |
| --- | --- |
| 테이블(table) | 인덱스(index) |
| 컬럼(column) | 필드(field) |
| 레코드(record), 로우(row) | 도큐먼트(document) |
| 스키마(schema) | 매핑(mapping) |

---

### **Elasticsearch 인덱스/매핑/도큐먼트 CRUD**

1. **인덱스 생성, 조회 및 삭제** 

```bash
# 인덱스 생성 : PUT /인덱스명
PUT /users

# 인덱스 조회 : GET /인덱스명
GET /users

# 인덱스 삭제 : DELETE /인덱스명
DELETE /users
```

1. **매핑 정의** 

```bash
# 매핑 정의 :
# PUT /인덱스명/_mappings 
# {
#   "properties": {
#     "필드명": { "type": "타입" },
#     "필드명": { "type": "타입" },
#     "필드명": { "type": "타입" }
#   }
# }
PUT /users/_mappings
{
    "properties" : {
        "name" : { "type" : "keyword" },
        "age" : { "type" : "integer" },
        "is_active" : { "type" : "boolean" }
    }
}

```

타입 keyword는 문자열 string과 비슷한 성질을 가진다. 

- 인덱스 생성 + 매핑 정의

```bash
# PUT /인덱스명 
# {
#  "mappings" : {
#     "properties" : {
#       "필드명" : { "type" : "타입" }
PUT /products 
{ 
  "mappings" : {
    "properties" : {
       "name" : { "type" : "text" }
     }
   }
}
```

1. **도큐먼트 저장**
- id를 자동으로 생성해서 저장

```bash
# 도큐먼트 저장 (id 자동 생성) : 
# POST /인덱스명/_doc
# {
#  "필드명": "타입에 일치한 값", 
#  "필드명": "타입에 일치한 값",
#  "필드명": "타입에 일치한 값"
# }
POST /users/_doc
{
  "name": "dbqudgns",
  "age": 25,
  "is_active": true
}
```

- id를 직접 지정해서 저장

```bash
# 도큐먼트 저장 (id 직접 지정) : id가 이미 존재한다면 에러
# POST /인덱스명/_create/id값 
# {
#  "필드명": "타입에 일치한 값", 
#  "필드명": "타입에 일치한 값",
#  "필드명": "타입에 일치한 값"
# }
POST /인덱스명/_create/1 
{ 
  "name" : "dbqudgns",
  "age" : 25,
  "is_active" : true
```

- id를 직접 저장해서 저장
    - 이미 id가 존재한다면 도큐먼트를 덮어씌움 == UPSERT

```bash
# 도큐먼트 저장 및 업데이트
# PUT /인덱스명/_doc/id값
# {
#  "필드명": "타입에 일치한 값", 
#  "필드명": "타입에 일치한 값",
#  "필드명": "타입에 일치한 값"
# }
PUT /users/_doc/2
{
	"name": "jason",
  "age": 30,
  "is_active": true
}
```

1. **도큐먼트 조회** 
- 모든 도큐먼트 조회

```bash
# GET /인덱스명/_search
GET /users/_search
```

- id로 특정 도큐먼트 조회

```bash
# GET /인덱스명/_doc/id값
GET /users/_doc/1
GET /users/_doc/2
```

1. **도큐먼트 수정** 
- 도큐먼트를 통째로 덮어 씌우기

```bash
# PUT /인덱스명/_doc/id값 == 도큐먼트 저장 시에도 사용
# {
#  "필드명": "타입에 일치한 값", 
#  "필드명": "타입에 일치한 값",
#  "필드명": "타입에 일치한 값"
# }
PUT /users/_doc/1
{
	"name" : "new"
}
```

- 일부 필드만 수정

```bash
# POST /인덱스명/_update/id값
# {
#   "doc" : {
#     "일부 필드명" : "타입에 일치한 값",
#     "일부 필드명" : "타입에 일치한 값"
#    }
# }
PUT /users/_update/1
{ 
   "doc" : {
     "age" : 10,
     "is_active" : false
    }
}
```

1. **도큐먼트 삭제**

```bash
# id로 도큐먼트 삭제 : DELETE /인덱스명/_doc/id값
DELETE /users/_doc/2
```

---

### **역 인덱스**

필드 값을 단어마다 쪼개서 찾기 쉽게 정리해 놓은 목록이다. 

이때, 필드 값에서 추출돼 역 인덱스에 저장된 단어를 토큰이라 한다. 

- ex : “Apple 2024 아이패드”를 검색한 경우

| **토큰(token)** | **도큐먼트 id** |
| --- | --- |
| **Apple** | **[1, 2, 3]** |
| 2025 | [1] |
| 맥북 | [1] |
| 에어 | [1] |
| 13 | [1] |
| M4 | [1] |
| 10코어 | [1] |
| **2024** | **[2, 3]** |
| 에어팟 | [2] |
| 4세대 | [2] |
| **아이패드** | **[3]** |
| mini | [3] |
| A17 | [3] |
| Pro | [3] |

Elasticsearch의 자체적인 로직으로 score(점수)를 매겨 score가 높은 순으로 도큐먼트를 조회한다. 

- 위 표에서는 도큐먼트(2) → 도큐먼트(3) → 도큐먼트(1) 순으로 조회된다.

보통 검색어와 관련성이 높을 수록 도큐먼트의 score(점수)가 높게 측정된다. 

따라서, Elasticsearch가 역 인덱스의 기능을 갖고 있기 때문에,

단어의 순서랑 상관없이 도큐먼트를 조회할 수 있다 !!

---

### **애널라이저 (Analyzer)**

정의 : 문자열(타입:text)을 토큰으로 변환시켜주는 장치 

- 위에서 언급한 단어 단위로 잘라 토큰을 만드는 것이 아닌 애널라이저가 여러 가지의 작업을 거쳐서 토큰을 만들어낸다.
  
- 애널라이저는 내부적으로 캐릭터 필터, 토크나이저, 토큰 필터를 활용해 문자열을 토큰으로 변환시킨다.

1. 캐릭터 필터(character filter)
- 문자열을 토큰으로 자르기 전에 문자열을 다듬는 역할
  
- 다양한 종류의 필터가 존재하며 여러 개의 필터를 적용시킬 수 있다.

2. 토크나이저(tokenizer) 
- 문자열을 토큰으로 자르는 역할
  
- 한 개의 토크나이저만 사용 가능하다.

ex) standard 토크나이저 : 공백 또는 . , ! ? 와 같은 문장 부호를 기준으로 자름 

Hello, my name is Byeong-Hun

⇒ [ Hello, my, name, is, Byeong, Hun ]

3. 토큰 필터(token filter)
- 잘린 토큰을 최종적으로 다듬는 역할
  
- 다양한 종류의 필터가 존재하며, 여러 개의 필터를 적용시킬 수 있다.

ex) lowercase 필터 : 소문자로 변환

Hello, my name is Byeong-Hun

⇒ [ hello, my, name, is, byeong, hun ]

Elasticsearch에 기본 값으로 설정되어 있는 애널라이저는 ? ⇒ Standard Analyzer 

- character filter는 설정되어 있지 않다
- tokenizer는 standard로 설정되어 있다.
- token filter는 lowercase로 설정되어 있다.

Standard Analyzer 표현 방식 

```json
// 방법 1
"analyzer" : "standard"

// 방법 2 
"char_filter" : [],
"tokenizer" : "standard",
"filter" : ["lowercase"]
```

Analyze API : text 타입 문자열을 어떤식으로 토큰화하였는지 알려주는 API 

```json
// 방법 1
GET /_analyze
{
	"text" : "_____",
	"analyzer" : "standard"
}

// 방법 2
GET /_analyze
{
	"text" : "_____",
	"char_filter" : [],
	"tokenizer" : "standard",
	"filter" : ["lowercase"]
}	
```

특정 인덱스의 필드에 적용된 Analyzer을 기반으 text 타입 문자열을 어떤식으로 토큰화하였는지 알려주는 API  

```json
GET /인덱스명/_analyze
{
  "field" : "필드명",
  "text" : "_____"
}
```

Custom Analyzer : 인덱스 생성 및 매핑 정의를 다같이 적용

```json
PUT /인덱스명
{
	"settings" : {
		"analysis" : {
		   "analyzer" : {
		     "커스텀 analyzer명" : {
				     "char_filter" : [],
				     "tokenizer" : "standard",
				     "filter" : []
				     }
				  }
			}
	},
	"mappings" : {
	   "properties" : {
	      "필드명" : {
	         "type" : "text",
	         "analyzer" : "커스텀 analyzer명" 
	         }
	      }
	  }
}          
```
---

### lowercase Token Filter

대소문자를 구분하지 않고 검색할 수 있게 만들기 위한 token filter 

만약, “Apple 2025 맥북 에어” 도큐먼트를 삽입하고 “Apple”을 검색하였다면?

- Analyze API를 통해 “Apple”은 “apple”로 토큰이 저장되어 있다.
  
- 하지만, “Apple”로 검색하면 왜 해당 도큐먼트가 조회가 되는 것일까 ?
    - 검색을 할 때도 Analyzer가 검색어로 입력한 문자열을 토큰으로 분리해 검색한다.
      
    - 따라서, “Apple”이라고 검색어를 입력하더라도 lowercase token filter에 의해 “apple”로 바뀐 채로 검색을 하게 된다.
        
결론적으로 Analyzer에서 lowercase token filter를 활용함으로써 대소문자에 상관없이 데이터를 검색할 수 있게 된 것이다. 

### html_strip Character Filter

html_strip : HTML 태그를 제거해주는 character filter 

- character filter에서 불필요한 HTML을 제거해줘 검색 속도가 향상되고 역인덱스에 불필요한 토큰을 저장하지 않아 디스크 공간을 낭비하지 않게 된다.

### stop Token Filter

stop : 검색할 때 필요 없는 불용어(a, an, the, or, but 등) 제거해주는 필터 

- 불용어를 활용해 검색할 일이 없는 데이터라면 ? ⇒ stop을 활용
- 불용어를 포함해서 검색하는게 중요한 데이터라면 ? ⇒ stop 적용 X

### stemmer Token Filter

stemmer : 단어의 형태(-ed, -ing, -s, -er 등)에 상관없이 검색해주는 필터 

- stemmer는 영단어를 기본형으로 변환한 후에 토큰으로 저장한다.

### synonym Token Filter

synonym : 동의어로 검색하게 해주는 필터 

- 상황 예시 : “Samsung Notebook”이라는 상품이 있다고 가정하자 사용자는 검색할 때 “노트북”, “랩탑”, 휴대용 컴퓨터”, “laptop”과 같이 동의어로 검색할 수 있다.

Custom Analyzer를 활용해 인덱스 생성

```json
PUT /인덱스명
{
	"settings" : {
		"analysis" : {
		   "filter" : {
		     "커스텀 filter명" : {
		       "type" : "synonym",
		       "synonyms" : [
		          "동의어1", "동의어2", "동의어3", ~
		       ]
		    }
		 },
		 "analyzer" : {
		     "커스텀 analyzer명" : {
		        "char_filter" : [],
		        "tokenizer" : "standard",
		        "filter" : [
		           "lowercase",
		           "커스텀 filter명"
		        ]
		      }
		   }
		}
 },
 "mappings" : {
	 "properties" : {
	    "필드명" : {
	       "type" : "text",
	       "analyzer" : "커스텀 analyzer명"
	     }
	   }
	 }
}
```

---

### Nori Analyzer

한글 문장을 토큰으로 분해하여 역인덱스에 저장해주는 애널라이저 

Analyze API 활용해 nori tokenizer 기본 구성 파악하기

```json
// 방법 1
GET /_analyze
{
  "text" : "한글 문장",
  "analyzer" : "nori"
}

// 방법 2
GET /_analyze
{ 
  "text" : "한글 문장",
  "char_filter" : [],
  "tokenizer" : "nori_tokenizer",
  "filter" : ["nori_part_of_speech", "nori_readingform", "lowercase"]
}
```

- nori_part_of_speech : 의미 없는 조사(을, 의 등), 접속사 제거
- nori_readingform : 한자를 한글로 바꿔서 토큰으로 저장

### 한글과 영어가 섞인 문장 검색

Analyze API를 활용해 한글과 영어가 섞인 문장을 토큰화 파악하기

```json
GET /_analyze
{
  "text" : "한글과 영어가 mixed된 문장"
  "char_filter" : [],
  "tokenizer" : "nori_tokenizer",
  "filter" : ["nori_part_of_speech", "nori_readingform", "lowercase" "stop", "stemmer"]
}
```

- 한글과 영어가 섞여있는 글이라면 Nori Analyzer를 활용하면 된다.
- 필드 값의 특징에 따라 char_filter 또는 token filter를 추가해서 조치를 취하면 된다.
