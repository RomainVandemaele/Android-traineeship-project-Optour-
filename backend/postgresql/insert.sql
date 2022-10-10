INSERT INTO clients(username,first_name,last_name,mail,password,pending,token) 
VALUES ('rvdemael','Romain','Vandemaele','romain.v@live.be','password',false,'aaaaaaaa');


INSERT INTO trips(trip_name,client_id,grade) VALUES ('test trip',3,5);

INSERT INTO points(point_name,latitude,longitude,trip_id,adress) 
VALUES  ('P1',50.1245745 , 4.2365874 ,4,'avenue gerard 45'),
		('P2',50.1245747 , 4.2365875 ,4,'avenue gerard 75'),
		('P3',50.1245752 , 4.2365871 ,4,'avenue gerard 55');

INSERT INTO trip_steps(start_point_id,end_point_id,step_time,step_length,step_order,step_mode,trip_id)
VALUES  (1,2,10,1.2,1,'FOOT',4),
        (2,3,20,6.5,2,'BIKE',4);

