import { AsyncResource } from "async_hooks";

// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript
//
const functions= require('firebase-functions');
const admin =require('firebase-admin');
admin.initializeApp();


exports.newUserCreated=functions.database.ref('/users/{uid}')
       .onCreate(async (snapshot, context) =>{
           let value=  snapshot.val();
           console.log(snapshot);
           console.log(value);
           let uid=context.params.uid;
           let phone=value.phone;
           let name=snapshot.val().name;
           let addUidByphone= admin.database().ref('/userUidByPhone/'+phone);
           addUidByphone.set(uid)                                                                                                                               
                        .then(function(){
                            console.log("Written in adduidbyphone, "+uid+", "+phone+", "+name);
                        })
                        .catch(function(){
                            console.log("error in adduidbyphone");
                        })
           let addNameByUid=admin.database().ref('/userNameByUid/'+uid);
           addNameByUid.set(name)  
                        .then(function(){
                            console.log("Written in addnamebyuid, "+uid+", "+phone+", "+name);
                        })
                        .catch(function(){
                            console.log("error in adduidbyphone");
                        })
            let addPhoneByUid=admin.database().ref('/userPhoneByUid/'+uid);
            addPhoneByUid.set(phone)  
                            .then(function(){
                                console.log("Written in addnamebyuid, "+uid+", "+phone+", "+name);
                            })
                            .catch(function(){
                                console.log("error in adduidbyphone");
                            })

       });
exports.newPendingSendRequestCreated=functions.database.ref("/users/{uid}/pendingSendRequest/{toUid}")
       .onCreate(async(snapshot, context) => {
           let value=snapshot.val();
           let uid=context.params.uid;
           let toUid=context.params.toUid;
           console.log(uid+", "+toUid);
           /** Add incoming request in touid */
           const touidIncomingreqRef=admin.database().ref("/users/"+toUid+"/incomingRequest/"+uid)
           touidIncomingreqRef.set(false)
           .then(function(){
               console.log("added");
           })
           .catch(function(){
               console.log("s");
           })
       });
exports.newIncomingRequestAdded=functions.database.ref("/users/{uid}/incomingRequest/{fromUID}")
       .onCreate(async (snapshot, context) => {
        const uid=context.params.uid;
        const fromUID=context.params.fromUID;
        const fromNameP= admin.database().ref("/users/"+fromUID+"/name").once('value');
        const fromName=await fromNameP.then(results=>{
                     return results.val();
        });
        console.log("from name:", fromName);
        const myTokenP= admin.database().ref("/users/"+uid+"/firebaseToken").once('value');
        const myToken=await myTokenP.then(results=>{
                     return results.val();
        });
        console.log("my token:", myToken);
        const payload={
          notification:{
            title:"Friend Request",
            body: fromName+" wants to add you."
          },
          data:{
              jio:"sevs"
          }
        }
        const response=await admin.messaging().sendToDevice(myToken, payload);
       })
exports.incomingRequestRemoved = functions.database.ref("/users/{uid}/incomingRequest/{fromUid}")
       .onDelete(async (snapshot, context) =>{
           console.log(snapshot.val())
           if(snapshot.val()== true)
               return
           const uid=context.params.uid;
           const fromuid=context.params.fromUid;
           console.log("1"+uid +",  " +fromuid);
           const removefromUidpendingRequestP= admin.database().ref("/users/"+fromuid+"/pendingSendRequest/"+uid).remove()
                                               .then(function(){
                                                   console.log("done");
                                               })
                                               .catch(function(){
                                                   console.log("errror");
                                               })
        
       })
exports.incomingRequestAccepted = functions.database.ref("/users/{uid}/incomingRequest/{fromUid}")
       .onUpdate(async (snapshot, context) =>{
             console.log(snapshot.after.val() +"<>"+snapshot.before.val());
             const uid=context.params.uid;
             const fromUid=context.params.fromUid;
             if(snapshot.after.val()== true)
             { 
                 //Remove from uid incoming Friend
                 const xyz= await admin.database().ref("/users/"+uid+"/incomingRequest/"+fromUid).remove()
                                .then(function(){
                                console.log("1done");
                            })
                            .catch(function(){
                                console.log("1errror");
                            })
                //Remove from FromUid pendingSend request
                const xyz1= await admin.database().ref("/users/"+fromUid+"/pendingSendRequest/"+uid).remove()
                            .then(function(){
                            console.log("2done");
                            })
                            .catch(function(){
                             console.log("2errror");
                            })
                //Add in friend of uid
                const xyz2= await admin.database().ref("/users/"+uid+"/friend/"+fromUid).set(true)
                                                .then(function(){
                                                    console.log("3done");
                                                    })
                                                    .catch(function(){
                                                    console.log("3errror");
                                                    })

                const xyz3= await admin.database().ref("/users/"+fromUid+"/friend/"+uid).set(true)
                                                .then(function(){
                                                    console.log("4done");
                                                    })
                                                    .catch(function(){
                                                    console.log("4errror");
                                                    })
                //Fetch FCM token
                const myNameP= admin.database().ref("/users/"+uid+"/name").once('value');
            const fromName=await myNameP.then(results=>{
                         return results.val();
            });
                const myTokenP= admin.database().ref("/users/"+fromUid+"/firebaseToken").once('value');
            const myToken=await myTokenP.then(results=>{
                         return results.val();
            });
            console.log("my token:", myToken);
            const payload={
              notification:{
                title:"Friend Request Accpeted",
                body: fromName+" accept your request"
              }
            }
            const response=await admin.messaging().sendToDevice(myToken, payload);
                                                                    

             }
        
       });
exports.transactionCreated=functions.database.ref('/transactions/{tid}')
       .onCreate(async (snapshot, context) => {
           const tidDetails=snapshot.val();
           console.log("created tid: ", context.params.tid);
           console.log("snap shot captured: ", tidDetails);
           const tid=context.params.tid;
           const touid=tidDetails.to;
           const fromuid=tidDetails.from;
           const addedbyuid=tidDetails.addedBy;
           const amount =tidDetails.amount;
           let tonotifyuid=null;
           let info;
           if(touid===addedbyuid)
              {
                  tonotifyuid=fromuid;
                  info="requested you to pay";
              }
           else
              {
                  tonotifyuid=touid;
                  info="wants to pay you";
              }
           const notifyTokenP= admin.database().ref("/users/"+tonotifyuid+"/firebaseToken").once('value');
           const notifyToken= await notifyTokenP.then(results=>{
              // console.log("results[0]", results.val());
               return results.val();
           });
           const addedBynameP=admin.database().ref("/users/"+addedbyuid+"/name").once('value');
           const addedByname=await addedBynameP.then(results=>{
               return results.val();
           });
           console.log("To notifications token: ", notifyToken);
           const payload = {
            notification:{
                title:"Transaction Requested",
                body: addedByname+" "+info+" "+amount
              }
          };
          const response = await admin.messaging().sendToDevice(notifyToken, payload);
          let pendingtransactionData={ [addedbyuid]:true, [tonotifyuid]:false};
          let reference=admin.database().ref("/users/"+touid+"/pendingTransactions/"+tid);
          reference.set(pendingtransactionData)
          .then(function(){
            console.log("Written");
          })
          .catch(function(){
            console.log("error");
          })
          let refTidInPending=admin.database().ref("/users/"+fromuid+"/pendingTransactions/"+tid);
          refTidInPending.set(pendingtransactionData)
          .then(function(){
            console.log("Writtendone");
          })
          .catch(function(){
            console.log("error");
          })
       });