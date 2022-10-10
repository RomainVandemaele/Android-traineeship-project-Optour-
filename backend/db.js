const Pool = require('pg').Pool


const pool = new Pool({
    user: 'postgres',
    host: 'localhost',
    database: 'TFE',
    password: 'postgres',
    port: 5432,
  })

module.exports.queryToPromise =   function queryToPromise(query,params) {

    return  new Promise( (resolve,reject) => {
        pool.query(query,params, (error,results) => {
            if(error) {
                reject(error)
            }else {
                resolve(results)
            }
        })

    })
}
