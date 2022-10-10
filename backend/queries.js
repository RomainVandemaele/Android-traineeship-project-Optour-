const Pool = require('pg').Pool
const db = require('./db.js')
const { response } = require('express')


require('dotenv').config()
// dotenv.config()


// const getAllQuery = (query,params) => {
//   db.queryToPromise(query,params)
//     .catch( (error) => {
//       console.log(error.message)
//       response.status(400).send(error.message)
//     })
//     .then( (results) => {
//       response.status(200).send(results.rows)
//     })
// }



const getClients = (request, response) => {
    db.queryToPromise('SELECT * FROM clients ORDER BY username',[])
      .catch( (error) => {
        console.log(error.message)
        response.status(400).send(error.message)
      })
      .then( (results) => {
        response.status(200).send(results.rows)
      })

 }


//GET
const getClientByUsername = (request, response) => {
    const username = request.params.username

    db.queryToPromise('SELECT * FROM clients WHERE username = $1', [username])
      .then( (results) => {
        response.status(200).send(results.rows[0])
      })
      .catch( (error) => {
        console.log("Error : " +error.message)
        response.status(400).send(error.message)
      })
}

const updateUsername = (request,response) => {
  const {id,username} = request.params
  console.log("UPDATE USERNAME "+ id + " to "+ username)

  db.queryToPromise('UPDATE clients SET username = $1 WHERE client_id = $2 RETURNING *',[username,id])
    .then( (results) => {
      response.status(201).send("updated")
    })
    .catch( (error) => {
      console.log(error.message)
      response.status(400).send(error.message)
    })

}

const updatePassword = (request,response) => {
  console.log("UPDATE PASSWORD "+ request.params)
  const {id,password} = request.params

  console.log("UPDATE PASSWORD "+ id+ " to "+password)

  db.queryToPromise('UPDATE clients SET password = $1 WHERE client_id = $2 RETURNING *',[password,id])
    .then( (results) => {
      response.status(201).send("updated")
    })
    .catch( (error) => {
      console.log("Error : " +error.message)
      response.status(400).send(error.message)
    })
}

const deleteClient = (request,response) => {

  console.log("DELETE CLIENT " + request.params)
  const id = parseInt(request.params.id)

  console.log("DELETE CLIENT "+ id)

  db.queryToPromise('DELETE FROM clients WHERE client_id = $1 RETURNING *',[id])
    .then( (results) => {
      response.status(201).send("deleted")
    })
    .catch( (error) => {
      console.log("Error : " +error.message)
      response.status(400).send(error.message)
    })
}



//POST
const createClient = (request, response) => {
  const { username,first_name, last_name, mail,password } = request.body
  console.log(first_name)

  const characters = '0123456789azertyuiopqsdfghkjlmwxcvbnAZERTYUIOPQSDFGHJKLMWXCVBN'
  let token = ''
  for (let i =0;i<8;++i) {
    token += characters[ Math.floor( Math.random()*characters.length ) ]
  }

  db.queryToPromise('INSERT INTO clients (username, first_name, last_name, mail,password,token) VALUES ($1, $2, $3, $4,$5,$6) RETURNING *', [username,first_name, last_name, mail,password, token])
      .then( (results) => {
        console.log(results)
        sendMail(mail,token)
        response.status(201).send(results.rows[0])
      })
      .catch( (error) => {
        console.log("Error : " + error.message)
        response.status(400).send(error.message)
      })

}

//POST
const createTrip = (request, response) => {
  const {trip_id,trip_name,client_id,grade, points, steps} = request.body
  console.log("CREATE TRIP")

  if(trip_id != null) {
    db.queryToPromise('DELETE FROM trips WHERE trip_id = $1',[trip_id])
      .then()
      .catch(error => {
        console.log(error.message)
      })

  }

  db.queryToPromise('INSERT INTO trips(trip_name,client_id,grade) VALUES ($1,$2,$3) RETURNING *',[trip_name,client_id,grade])
    .then( (trip) => {
        console.log(trip)
        const {trip_id} = trip.rows[0]
        console.log(trip_id)
        console.log(points)

        
        insertPoints(points,trip_id)
        .catch((error)  => console.log("Error points " +error.message) )
        .then( (pointsId) => {
          console.log("steps")
          insertSteps(steps, pointsId,trip_id)
            .catch((error)  => console.log("Error points " +error.message) )
            .then( (pointsI) => {

              response.status(201).send(""+trip_id)
            }
               )
        })
    })
    .catch( (error) => {
      console.log("ERROR")
      console.log(error.message)
      response.status(400).send(error.message)
    })
}

const insertPointsAndSteps = (points,steps,trip_id) => {

 
}


const insertSteps = async function(steps,pointsId,trip_id) {
  let i = 0
  for(step of steps) {
    const {step_time,step_length,step_order,step_mode} = step
    await db.queryToPromise('INSERT INTO trip_steps(start_point_id,end_point_id,step_time,step_length,step_order,step_mode,trip_id) VALUES ($1,$2,$3,$4,$5,$6,$7) RETURNING *',
    [ pointsId[i], pointsId[i+1], step_time,step_length,step_order,step_mode, trip_id ])
    i+=1
  }
}

const insertPoints = async function(points,trip_id) {
  const pointsId =[]

  for(point of points) {
    const {point_name,latitude,longitude,adress,place_id} = point
    const points = await db.queryToPromise('INSERT INTO points(point_name,latitude,longitude,trip_id,adress,place_id) VALUES ($1,$2,$3,$4,$5,$6) RETURNING *'
    ,[point_name,latitude,longitude,trip_id,adress,place_id])
        .then( (point) => {
          console.log("new point")
          pointsId.push(point.rows[0].point_id)

        })
        .catch( (error) => console.log("Error one point " + error.message))

  }
  
  return pointsId
}





//UPDATE
const activateAccount= (request, response) => {

  const clientid = parseInt(request.params.id)

  db.queryToPromise('UPDATE clients SET pending= $1 WHERE client_id = $2',[false,clientid])
      .catch( (error) => {
        console.log("Error " + error.message)
        response.status(400).send(error.message)
      })
      .then( (result) => {
        console.log(`User modified with ID: ${result.client_id}`)
        response.status(201).send("activated")
      })

}


//GET trip
const getAllPoints= (request, response) => {
  
  db.queryToPromise('SELECT * FROM points',[])
    .catch( (error) => {
      console.log(error.message)
      response.status(400).send(error.message)
    })
    .then( (results) => {
      response.status(200).send(results.rows)
    })

}


//GET trips
const getAllTrip = (request, response) => {
  
  db.queryToPromise('SELECT * FROM trips',[])
    .catch( (error) => {
      console.log(error.message)
      response.status(400).send(error.message)
    })
    .then( (results) => {
      response.status(200).send(results.rows)
    })

}


//GET trips of one client
const getAllTripByClientId = (request, response) => {

  const clientId = request.params.clientId
  console.log("Id : " +clientId)
  db.queryToPromise(`
    SELECT T.trip_id, T.trip_name,T.client_id,T.grade , P.adress as start_adress , P2.adress as end_adress
    FROM trips T LEFT JOIN  points P ON P.trip_id = T.trip_id LEFT JOIN  points P2 ON P2.trip_id = T.trip_id
    WHERE T.client_id = $1 AND P.point_id = (SELECT MIN(point_id) FROM points WHERE trip_id = T.trip_id ) AND P2.point_id = (SELECT MAX(point_id) FROM points WHERE trip_id = T.trip_id )`
    ,[clientId])
    .then( (results) => {
      response.status(200).send(results.rows)
    })
    .catch( (error) => {  
      console.log(error.message)
      response.status(400).send(error.message)
    })

}

//GET trips of one client
const getAllTripByText = (request, response) => {

  const query = request.params.query
  console.log("query : " +query)
  db.queryToPromise(`
    SELECT T.trip_id, T.trip_name,T.client_id,T.grade , P.adress as start_adress , P2.adress as end_adress
    FROM trips T LEFT JOIN  points P ON P.trip_id = T.trip_id LEFT JOIN  points P2 ON P2.trip_id = T.trip_id
    WHERE UPPER(trip_name) LIKE \'\%${query.toUpperCase()}\%\' AND P.point_id = (SELECT MIN(point_id) FROM points WHERE trip_id = T.trip_id ) AND P2.point_id = (SELECT MAX(point_id) FROM points WHERE trip_id = T.trip_id )`
    ,[])
    .then( (results) => {
      //console.log(results)
      response.status(200).send(results.rows)
    })
    .catch( (error) => {  
      console.log("Error" + error.message)
      response.status(400).send(error.message)
    })

}

const getAllSteps = (request,response) => {
  db.queryToPromise('SELECT * FROM trip_steps',[])
    .catch( (error) => {
      console.log(error.message)
      response.status(400).send(error.message)
    })
    .then( (steps) => {
      response.status(200).send(steps.rows)
    })
}



/**
 * SELECT * 
FROM  trips JOIN trip_steps ON trip_steps.trip_id = trips.trip_id  RIGHT JOIN points ON  trip_steps.start_point_id = points.point_id   RIGHT JOIN points P2 ON  trip_steps.end_point_id = P2.point_id
WHERE trips.trip_id = 17
 * @param {*} request 
 * @param {*} response 
 */
//GET trip
const getTrip = (request, response) => {
  
  console.log("getTrip")
  const id = parseInt(request.params.id)
  console.log("id : " + id)

  let trip = ""

  db.queryToPromise('SELECT * FROM trips WHERE trip_id = $1',[id])
    .catch( (error) => {
      console.log("Error : " + error.message)
      response.status(400).send(error.message)
    })
    .then( (result) => {
      //response.status(200).send(result)
      //console.log(result)

      if(result.rows.length == 0) response.status(404).send({msg: "Not found"})
      else {
        trip = result.rows[0]
        db.queryToPromise('SELECT * FROM trip_steps WHERE trip_id = $1',[id])
          .catch( (err) => console.log("error steps"))
          .then( (steps) => {
            console.log("ok steps")
            trip.steps = steps.rows

            db.queryToPromise('SELECT * FROM points WHERE trip_id = $1',[id])
              .catch( (err) => console.log("error steps"))
              .then( (points) => {
                trip.points = points.rows
                response.status(200).send(trip)
              })
            
          })
        }
    })

}




const sgMail = require('@sendgrid/mail');
const { SENDGRID_API_KEY2 } = require('./sendGrid.js')
const { crossOriginResourcePolicy } = require('helmet')



const sendMail = (email,token) => {

    sgMail.setApiKey(SENDGRID_API_KEY2);
    const msg = {
        to: email.toString(),
        from: 'rvdemaeldigitalcity@gmail.com', // Use the email address or domain you verified above
        subject: 'Optour : Confirm your mail',
        html: '<strong>Welcome to Optour</strong> <p>Confirm your mail by entering the following code in the app :<br/> ' + token.toString() +'<br/><br/>See you soon<br/>Romain Vandemaele<br/>CEO Optour</p>',
    };

    (async () => {
        try {
            await sgMail.send(msg);
        } catch (error) {
            console.error(error);
            if (error.response) {
                console.error(error.response.body)
            }
        }
    })();

}


const deleteTrip = (request,response) => {
  const id = parseInt(request.params.id)
  
  db.queryToPromise("delete FROM trips WHERE trip_id = $1 RETURNING *",[id])
    .then( (results) => {

        console.log(results)
        response.status(201).send(results.rows[0])
    })
    .catch( (err) => {
      console.log("Error : " +err)
      response.status(401).send(err.message)
  })
}

const getCommentsByTrip = (request,response) => {
  const tripId = request.params.tripId
  db.queryToPromise("SELECT C.comment, CL.username FROM comments C LEFT JOIN clients CL ON C.client_id = CL.client_id WHERE C.trip_id = $1",[tripId])
    .then( (results) => {
      response.status(200).send(results.rows)
    })
    .catch( (err) => {
      response.status(401).send(err.message)
    })

}


const postComment = (request,response) => {
    const {trip_id,client_id,comment} = request.body 
    console.log("POST COMMENT")
    db.queryToPromise("INSERT INTO comments(trip_id,client_id,comment) VALUES ($1,$2,$3) RETURNING *",[trip_id,client_id,comment])
      .then( (result) => {
          response.status(201).send("Comment inserted")
      })
      .catch( (err) => {
        console.log("Error : " + err.message)
        response.status(401).send(err.message)
      })
}

module.exports = {
    getClients,
    getClientByUsername,
    createClient,
    sendMail,
    activateAccount,
    getAllTrip,
    getAllPoints,
    getTrip,
    getAllSteps,
    createTrip,
    getAllTripByClientId,
    deleteTrip,
    getAllTripByText,
    getCommentsByTrip,
    postComment,
    updatePassword,
    deleteClient,
    updateUsername
}



