const express = require('express')
const bodyParser = require('body-parser')
const cors = require('cors');
const helmet = require('helmet');
const morgan = require('morgan');
const {auth} = require('express-openid-connect');

//var jwt = require('express-jwt');
const { expressjwt: expressJwt } = require('express-jwt');
var jwks = require('jwks-rsa');


// const config = {
//   authRequired: false,
//   auth0Logout: true,
//   baseURL: 'http://localhost:3000',
//   clientID: 'eY14uAjCWVcCbiTBhF2rK3wIBZHbo9xk',
//   issuerBaseURL: 'dev-m2pdqwuh.eu.auth0.com',
//   secret: 'nkPK4NYJRHmBMLC1V7zitBwbjzBHQEnfsa7t5uTgCFHpbnM'
// };



const app = express()

// auth router attaches /login, /logout, and /callback routes to the baseURL
//app.use(auth(config))


const port = 3000

const db = require('./queries')

// adding Helmet to enhance your API's security
app.use(helmet());

// using bodyParser to parse JSON bodies into JS objects
app.use(bodyParser.json())
app.use(
  bodyParser.urlencoded({
    extended: true,
  })
)

// enabling CORS for all requests
app.use(cors());

// adding morgan to log HTTP requests
app.use(morgan('combined'));



app.get('/', (request, response) => {
    response.json({ info: 'Node.js, Express, and Postgres API' })
    //response.send(request.oidc.isAuthenticated() ? 'Logged in' : 'Logged out')
})


app.post('/clients', db.createClient)
app.get('/clients', db.getClients)
app.get('/clients/:username', db.getClientByUsername)

app.patch('/client/:id/username/:username', db.updateUsername)
app.patch('/client/:id/password/:password', db.updatePassword)
app.delete('/client/:id',db.deleteClient)

app.post('/clients/:id', db.activateAccount)

app.post('/trip',db.createTrip)
app.get('/trips',db.getAllTrip)
app.get('/points',db.getAllPoints)
app.get('/trip/:id',db.getTrip)

app.get('/trips/:clientId',db.getAllTripByClientId)
app.get('/trips/text/:query',db.getAllTripByText)
app.get('/steps/',db.getAllSteps)

app.get('/comments/:tripId',db.getCommentsByTrip)
app.post('/comment',db.postComment)

app.delete('/trip/:id',db.deleteTrip)

//every request defined after need a token



// var jwtCheck = expressJwt({
//     secret: jwks.expressJwtSecret({
//         cache: true,
//         rateLimit: true,
//         jwksRequestsPerMinute: 60,
//         jwksUri: 'https://dev-m2pdqwuh.eu.auth0.com/.well-known/jwks.json'
//   }),
//   audience: 'https://tfe-api',
//   issuer: 'https://dev-m2pdqwuh.eu.auth0.com/',
//   algorithms: ['RS256']
// });

//app.use(jwtCheck)



app.get('/authorized', function (req, res) {
  res.send('Secured Resource');
  
});

//app.post('/users', db.createUser)
//app.put('/users/:id', db.updateUser)
//app.delete('/users/:id', db.deleteUser)


app.listen(port, () => {
    console.log(`App running on port ${port}.`)
})

