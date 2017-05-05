# 1. FireBaseChat

## 출력화면

--> FireBase를 사용하여 간단한 채팅방을 구현하였습니다.

![](http://i.imgur.com/NdCJVFP.png)

--> 로그인 화면입니다. 사용자가 입력한 아이디와 비밀번호를 가지고 firebase의 realtime database에 있는 정보들과 비교합니다.

![](http://i.imgur.com/ry5qDdC.png)

--> 사용자가 로그인을 하면, 미리 만들어둔 채팅방의 목록이 화면에 리스트로 나타납니다.

![](http://i.imgur.com/Ui5RExC.png)

--> 사용자가 채팅방을 누르면, 채팅창으로 화면이 바뀝니다. 이 때, 사용자가 누른 채팅방의 키값과 사용자의 아이디, 사용자 이름, 메시지가 realtime database에 저장됩니다.

>> 코드의 핵심적인 부분 위주로 설명드리겠습니다.

## 1.1 MainActivity.java

--> 사용자가 로그인을 하는 화면입니다. FireBase에 있는 데이터를 불러와서 값을 비교하는 부분입니다.

[MainActivity.java]

![](http://i.imgur.com/QWgGa1Q.png)
![](http://i.imgur.com/Zb9N0mn.png)

(1) FireBase와 먼저 연결합니다. userRef라는 참조변수는 현재 "fir-one-ed3ed" 아래 "user" 아래 데이터들을 가리키고 있는 상태입니다. 

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("user");

![](http://i.imgur.com/NPF7uRv.png)


(2) FireBase로 child(id) 레퍼런스에 대한 쿼리를 날립니다. userRef.child(id) 검색에 대한 쿼리문 명령어입니다.

		userRef.child(id).addListenerForSingleValueEvent(new ValueEventListener()
					.........................
					.........................
					.........................
		});

예를 들어 만약 사용자가 ID : aaa, 비밀번호 : 555입력하는 경우 위 쿼리문은 키값 "aaa" 아래 내용들을 가져옵니다.

		예) 		aaa
				 ---- name: "홍길동"
				 ---- password: 123

(3) FireBase는 데이터쿼리가 완료되면 스냅샷에 담아서 onDataChange를 호출해줍니다.

		public void onDataChange(DataSnapshot dataSnapshot){
				...................
				...................
		}

(4) (3)에서 가져온 데이터와 사용자가 입력한 아이디, 비밀번호를 비교하는 로직입니다.

	if(dataSnapshot.getChildrenCount() > 0){	// dataSnapshot은 사용자가 요청한 쿼리문 데이터정보를 가지고 있습니다. 만약 데이터가 1개 이상이라면 현재 파이어베이스에 사용자의 아이디가 존재한다는 의미입니다.
	        String fbPw = dataSnapshot.child("password").getValue().toString();
	        String name = dataSnapshot.child("name").getValue().toString();
	        if(fbPw.equals(pw)){				// 현재 dataSnapshot 아래 password 키값에 대응하는 value가 사용자의 비밀번호와 일치하는지 확인합니다.
	            Intent intent = new Intent(MainActivity.this, RoomListActivity.class);       		
	            intent.putExtra("userid",id);
	            intent.putExtra("username",name);  // 사용자 비밀번호가 맞다면, 사용자 아이디와 이름을 intent에 담아 넘깁니다.
	            startActivity(intent);
	        } else {
	            Toast.makeText(MainActivity.this, "Wrong PassWord", Toast.LENGTH_SHORT).show();
	        }
    } else {
        	Toast.makeText(MainActivity.this, "Nothing", Toast.LENGTH_SHORT).show();
    }	

## 1.2 RoomListActivity.java + ListAdapter.class

--> 채팅방의 목록을 보여줍니다.

[RoomListActivity.java]

![](http://i.imgur.com/FYOuWpT.png)
![](http://i.imgur.com/w5E7UTJ.png)
![](http://i.imgur.com/4twnixU.png)

(1) FireBase와 먼저 연결합니다. roomRef라는 참조변수는 현재 "fir-one-ed3ed" 아래 "room" 아래 데이터들을 가리키고 있는 상태입니다. 

 		       FirebaseDatabase database = FirebaseDatabase.getInstance();
 		       DatabaseReference roomRef = database.getReference("room");

![](http://i.imgur.com/Y8xaxOi.png)

(2) 리스트뷰와 연결 및 어텝터를 세팅합니다.

     	       listView = (ListView)findViewById(R.id.listView);
		       adapter = new ListAdapter(datas, this);
		       listView.setAdapter(adapter);


(3) 채팅방이 클릭 되었을 때를 처리하는 리스너입니다. 인텐트에 선택된 방의 키값과 방이름, 사용자 아이디와 사용자 이름을 담아 다음 액티비티에 전달합니다.

				listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			            @Override
            			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				                Room room = datas.get(position);
				                Intent intent = new Intent(RoomListActivity.this, RoomActivity.class);
				                intent.putExtra("key", room.getKey());
				                intent.putExtra("title",room.getTitle());
				                intent.putExtra("userid", userid);
				                intent.putExtra("username", username);
				                startActivity(intent);
            			}
				}

(4) 경로의 전체 내용에 대한 변경(방의 생성 혹은 삭제)을 읽고 수신 대기합니다.

				roomRef.addValueEventListener(roomListener);


(5) addValueEventListener() 리스너를 처리합니다.
	datas는 방정보(Room.class)를 담고있는 List입니다. dataSnapshot이 가리키는 방의 키값과 value 값을 List 타입의 datas에 각각 setKey()와 setTitle로 저장합니다. 그러고나서 어뎁터에 변경사항을 notify하면 미리 세팅된 어뎁터가 현재 datas 정보를 화면에 출력시켜줍니다. 





				    ValueEventListener roomListener = new ValueEventListener() {
					        @Override
					        public void onDataChange(DataSnapshot dataSnapshot) {
					
					            datas.clear();
					            for( DataSnapshot snapshot : dataSnapshot.getChildren() ) {
					                Room room = new Room();
					                room.setKey(snapshot.getKey());
					                room.setTitle(snapshot.getValue().toString());
					                datas.add(room);
					            }
					            adapter.notifyDataSetChanged();
					        }
							......................
			    	};


(6) ListAdapter.class

--> 채팅방 목록을 보여주기 위해 사용합니다. ( week4-1 참조 : [https://github.com/Ronal92/MyAndroidLectures/blob/master/week4/week4-1.md](https://github.com/Ronal92/MyAndroidLectures/blob/master/week4/week4-1.md) )

[ListAdapter.class]

![](http://i.imgur.com/YlMWWn8.png)
![](http://i.imgur.com/0U9DVc4.png)


## 1.3 RoomActivity.java + CustomAdapter.class

--> 사용자가 채팅방을 누르면 채팅장 화면을 보여줍니다.

[RoomActivity.java ]

![](http://i.imgur.com/B9SDMWu.png)
![](http://i.imgur.com/uyaRWyz.png)
![](http://i.imgur.com/RKUtQiC.png)

(1) RoomListActivity.java에서 전달한 방의 키값과 방이름, 사용자 아이디와 사용자 이름을 꺼냅니다.

        Intent intent = getIntent();
        String key = intent.getExtras().getString("key");
        String title = intent.getExtras().getString("title");
        userid = intent.getExtras().getString("userid");
        username = intent.getExtras().getString("username");




(2) FireBase와 먼저 연결합니다. roomRef라는 참조변수는 현재 "chat" 아래 특정 채팅방 아래 데이터들을 가리키고 있는 상태입니다. 

 		       FirebaseDatabase database = FirebaseDatabase.getInstance();
 		       DatabaseReference roomRef = database.getReference("chat").child(key);

![](http://i.imgur.com/87pQ5iL.png)

(3) 경로의 전체 내용에 대한 변경(메시지 생성)을 읽고 수신 대기합니다.

					roomRef.addValueEventListener(eventListener);

(4) addValueEventListener() 리스너를 처리합니다. 현재 dataSnapshot이 가리키고 있는 데이터들을 Message.class 구조에 맞게 변환시킨뒤, msg에 저장합니다. msg는 다시 List 타입의 datas에 저장됩니다. 

			ValueEventListener eventListener = new ValueEventListener() {
			        @Override
			        public void onDataChange(DataSnapshot dataSnapshot) {
			            datas.clear();
			            for( DataSnapshot snapshot : dataSnapshot.getChildren() ) {
			                String key  = snapshot.getKey();
			                Message msg = snapshot.getValue(Message.class);
			                msg.key = key;
			                datas.add(msg);
			
			            }
			            adapter.notifyDataSetChanged();
			        }
			
			        @Override
			        public void onCancelled(DatabaseError databaseError) {
			
			        }
		    };

(5) 메세지 전송 버튼의 상태를 읽고 수신 대기 합니다.

				btnSend.setOnClickListener(sendListener);

(6) (5) 버튼 리스너 처리를 합니다. 현재 roomRef가 가리키고 있는 데이터 바로 아래 노드에 키값을 주고 Map 데이터 구조로 사용자가 입력한 메세지 정보를 저장합니다. 최종적으로 setValue()를 사용해서 FireBase 서버에 전송합니다.


			    View.OnClickListener sendListener = new View.OnClickListener(){
				        @Override
				        public void onClick(View v) {
				            DatabaseReference msgRef = roomRef.push();
				            String msg = editMessage.getText().toString();
				
				            Map<String, String> msgMap = new HashMap<>();
				            msgMap.put("userid",userid);
				            msgMap.put("username",username);
				            msgMap.put("msg",msg);
				
				            msgRef.setValue(msgMap);
				        }
			    };   

![](http://i.imgur.com/vA8nUfg.png)

(7) CustomAdapter.class

--> 채팅방에 있는 메세지 목록을 보여주기 위해 사용합니다. ( week4-1 참조 : [https://github.com/Ronal92/MyAndroidLectures/blob/master/week4/week4-1.md](https://github.com/Ronal92/MyAndroidLectures/blob/master/week4/week4-1.md) )



[CustomAdapter.class] 

![](http://i.imgur.com/714pFPY.png)
![](http://i.imgur.com/dRZcLbq.png)