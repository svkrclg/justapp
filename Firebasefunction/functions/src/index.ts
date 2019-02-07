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
              },
              data:{
                code: "001"
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
exports.actionOnPendingTransactions=functions.database.ref("/users/{uid}/pendingTransactions/{ptid}")
       .onUpdate(async(change, context) => {
              let uid=context.params.uid;
              let ptid=context.params.ptid;
              let beforeShot=change.before.val();
              let afterShot= change.after.val();
              let uidArr=Object.keys(beforeShot);
              let uid1=uidArr[0];
              let uid2=uidArr[1];
              let uid1valueB=change.before.child(uid1).val();
              let uid2valueB=change.before.child(uid2).val();
              let uid1valueA=change.after.child(uid1).val();
              let uid2valueA=change.after.child(uid2).val();
              console.log(uid1, "----", uid2, "----",uid1valueB, "----", uid2valueB);
              //if the change is performed by addedby
              if((uid1valueB===true &&uid1===uid)|| (uid2valueB===true &&uid2===uid))
              {
                //Deleted by addedby
                console.log("Change by addedby");
                if((uid1valueA===false &&uid1===uid)|| (uid2valueA===false &&uid2===uid))
                 {
                  console.log("Change by addedby for delete");
                   let opponentuid;
                   if(uid1===uid)
                    opponentuid=uid2;
                   else 
                    opponentuid=uid1; 
                   //Remove pending transactions from addedby
                   const addedByPendingTrans=admin.database().ref("/users/"+uid+"/pendingTransactions/"+ptid);
                   addedByPendingTrans.remove().then(function(){
                     console.log("removed from addedby");
                   }).catch(function(error){
                     console.log("error");
                   });
                   //Delete in opponent
                   const opponentPendingTrans=admin.database().ref("/users/"+opponentuid+"/pendingTransactions/"+ptid);
                   opponentPendingTrans.remove().then(function(){
                     console.log("removed from opponent");
                   }).catch(function(error){
                     console.log("error");
                   });
                   const removefromTransactions=admin.database().ref("/transactions/"+ptid);
                   removefromTransactions.remove().then(function(){
                     console.log("removed from tranactions");
                   }).catch(function(error){
                     console.log("error");
                   });
                  
                  
                 }
                }
                //change by performed by opponent
              else if((uid1valueB===true && uid1!== uid) ||(uid2valueB===true && uid2!== uid)) {
                //Deleted by opponent
                console.log("Change by opponent");
                if((uid1valueB===true && uid1valueA===false  && uid1!==uid) || (uid2valueB===true && uid2valueA===false  && uid2!==uid)) 
                {
                  console.log("Change by opponet for delete");
                     let tonotifyuid;
                     if(uid1valueB===true)
                        tonotifyuid=uid1;
                     else  
                        tonotifyuid=uid2;
                     console.log("toNotifyuid for delete: ", tonotifyuid);
                     const removefromopponent=admin.database().ref("/users/"+uid+"/pendingTransactions/"+ptid);
                     removefromopponent.remove().then(function(){
                       console.log("Removed from opponent,");
                     }).catch(function(error){
                       console.log("error");
                     })
                     const toNotifyuidToken=await admin.database().ref("/users/"+tonotifyuid+"/firebaseToken").once('value')
                                            .then(function(dataspht){
                                                    return dataspht.val();
                                            });
                     const uidname=await admin.database().ref("/users/"+uid+"/name").once('value')
                                            .then(function(dataspht){
                                                    return dataspht.val();
                                            });                      
                     const removefromaddedby=admin.database().ref("/users/"+tonotifyuid+"/pendingTransactions/"+ptid);
                     removefromaddedby.remove().then(function(){
                       console.log("Removed from addedby,");
                     }).catch(function(error){
                       console.log("error");
                     });
                     const removefromtransactions=admin.database().ref("/transactions/"+ptid);
                     removefromtransactions.remove().then(function(){
                       console.log("Removed from tranactions,");
                     }).catch(function(error){
                       console.log("error");
                     })

                     console.log("my token:", toNotifyuidToken);
                     const payload={
                       notification:{
                         title:"Your transaction request rejected",
                         body: uidname+" rejected your transaction."
                       },
                       data:{
                           jio:"sevs"
                       }
                     }
                     const response= admin.messaging().sendToDevice(toNotifyuidToken, payload);


                } 
                //Accepted By opponent
                if((uid1valueB===false && uid1valueA===true &&uid1===uid) ||(uid2valueB===false && uid1valueA===true &&uid2===uid))
                {
                  console.log("Change by opponeent for accept");
                  let tonotifyuid;
                  if(uid1valueB===true)
                        tonotifyuid=uid1;
                  else  
                        tonotifyuid=uid2;
                 console.log("tonotifyuid for accept: ", tonotifyuid);
                 const removefromopponent=admin.database().ref("/users/"+uid+"/pendingTransactions/"+ptid);
                  removefromopponent.remove().then(function(){
                    console.log("Removed from opponent,");
                  }).catch(function(error){
                    console.log("error");
                  });
                 const removefromaddedby=admin.database().ref("/users/"+tonotifyuid+"/pendingTransactions/"+ptid);
                  removefromaddedby.remove().then(function(){
                    console.log("Removed from addedby,");
                  }).catch(function(error){
                    console.log("error");
                  });
                  const toNotifyuidToken=await admin.database().ref("/users/"+tonotifyuid+"/firebaseToken").once('value')
                                            .then(function(dataspht){
                                                    return dataspht.val();
                                            });
                     const uidname=await admin.database().ref("/users/"+uid+"/name").once('value')
                                            .then(function(dataspht){
                                                    return dataspht.val();
                                            });    
                 //getting the snapshot of transaction which is going to be deleted further
                 const gettransactionshot= await admin.database().ref("/transactions/"+ptid).once('value')
                 .then(function(dataSnapshot){
                   console.log(dataSnapshot.val());
                   var shot=dataSnapshot.val(); 
                   var aUid=shot.addedBy;
                   var amt=shot.amount;
                   var fUid=shot.from;
                   var rsn=shot.reason;
                   var tUid=shot.to;
                   var timeStamp=Date.now();
                   console.log("Test: "+aUid);
                   console.log("Test: "+aUid.toString());
                   return {addedBy: aUid, amount: amt, from: fUid, reason:rsn, to:tUid, confirmTimeStamp:timeStamp };
                 })
                 
                 const removefromtransactions=admin.database().ref("/transactions/"+ptid);
                  removefromtransactions.remove().then(function(){
                    console.log("Removed from tranactions,");
                  }).catch(function(error){
                    console.log("error");
                  })
                
                let reference=admin.database().ref("/confirmedTransactions/"+ptid);
                reference.set(gettransactionshot)
                         .then(function(){
                            console.log("Written");
                         })
                        .catch(function(){
                            console.log("error");
                         })
                console.log("my token:", toNotifyuidToken);
                const payload={
                  notification:{
                    title:"Your transaction request rejected",
                    body: uidname+" accepted your transaction."
                  },
                  data:{
                      jio:"sevs"
                  }
                }
                const response= admin.messaging().sendToDevice(toNotifyuidToken, payload);


                }
              }  
       });
exports.handleEachConfirmedTransactions=functions.database.ref("/confirmedTransactions/{tid}")
       .onCreate(async(snapshot, context) => {
          console.log("transactions confirmed: ", snapshot.val());
          const tid=context.params.tid;
          const addedbyuid=snapshot.val().addedBy;
          const touid=snapshot.val().to;
          const fromuid=snapshot.val().from;
          let amount=snapshot.val().amount;
          //add the transaction in addedby
          if(addedbyuid===touid)
          {
             //insert in addedby
            let preamountA= await admin.database().ref("/users/"+addedbyuid+"/myTransactions/"+fromuid+"/netTotal").once('value')
            .then(function(dataSnapshot){
              if(dataSnapshot.val()===null)
                   return 0;
              else 
                 return dataSnapshot.val();
            })
            
            let newamountA=amount+preamountA;
            let totalrefA=admin.database().ref("/users/"+addedbyuid+"/myTransactions/"+fromuid+"/netTotal");
            totalrefA.set(newamountA)
            .then(function(){
              console.log("total written");
              })
              .catch(function(){
              console.log("errortotal");
              })
            let transactionrefA=admin.database().ref("/users/"+addedbyuid+"/myTransactions/"+fromuid+"/transactions/"+tid);
            transactionrefA.set(true)
            .then(function(){
                console.log("total written");
                })
                .catch(function(){
                console.log("errortotal");
                })
            //insert in opponent
            let preamountO= await admin.database().ref("/users/"+fromuid+"/myTransactions/"+touid+"/netTotal").once('value')
            .then(function(dataSnapshot){
              if(dataSnapshot.val()===null)
                   return 0;
              else 
                 return dataSnapshot.val();
            })
            
            let newamountO=preamountO-amount;
            let totalrefO=admin.database().ref("/users/"+fromuid+"/myTransactions/"+touid+"/netTotal");
            totalrefO.set(newamountO)
            .then(function(){
              console.log("total written");
              })
              .catch(function(){
              console.log("errortotal");
              })
            let transactionrefO=admin.database().ref("/users/"+fromuid+"/myTransactions/"+touid+"/transactions/"+tid);
            transactionrefO.set(true)
            .then(function(){
                console.log("total written");
                })
                .catch(function(){
                console.log("errortotal");
                })
          }
          else
          {
             //insert in addedby
            let preamountA= await admin.database().ref("/users/"+addedbyuid+"/myTransactions/"+touid+"/netTotal").once('value')
            .then(function(dataSnapshot){
              console.log(dataSnapshot.val());
              if(dataSnapshot.val()===null)
                   return 0;
              else 
                 return dataSnapshot.val();
            })
            
            let newamountA=preamountA-amount;
            let totalrefA=admin.database().ref("/users/"+addedbyuid+"/myTransactions/"+touid+"/netTotal");
            totalrefA.set(newamountA)
            .then(function(){
              console.log("total written");
              })
              .catch(function(){
              console.log("errortotal");
              })
            let transactionrefA=admin.database().ref("/users/"+addedbyuid+"/myTransactions/"+touid+"/transactions/"+tid);
            transactionrefA.set(true)
            .then(function(){
                console.log("total written");
                })
                .catch(function(){
                console.log("errortotal");
                })
            //insert in opponent
            let preamountO= await admin.database().ref("/users/"+touid+"/myTransactions/"+addedbyuid+"/netTotal").once('value')
            .then(function(dataSnapshot){
              if(dataSnapshot.val()===null)
                   return 0;
              else 
                 return dataSnapshot.val();
            })
            
            let newamountO=amount+preamountO;
            let totalrefO=admin.database().ref("/users/"+touid+"/myTransactions/"+addedbyuid+"/netTotal");
            totalrefO.set(newamountO)
            .then(function(){
              console.log("total written");
              })
              .catch(function(){
              console.log("errortotal");
              })
            let transactionrefO=admin.database().ref("/users/"+touid+"/myTransactions/"+addedbyuid+"/transactions/"+tid);
            transactionrefO.set(true)
            .then(function(){
                console.log("total written");
                })
                .catch(function(){
                console.log("errortotal");
                })
          }

       });
exports.qrcode=functions.database.ref("/users/{uid}/qrcode/{oppnuid}")
               .onCreate(async(snapshot, context) =>{
                      let uid=context.params.uid;
                      let oppnuid=context.params.oppnuid;
                      console.log(uid +" "+oppnuid+ " "+ snapshot.val());
                      if(snapshot.val()===false){
                      let promise3=admin.database().ref("/users/"+uid+"/qrcode/"+oppnuid);
                        promise3.set(true)
                           .then(function(){
                              console.log(uid+ "..."+ oppnuid+"..."+snapshot.val());
                            })
                            .catch(function(){
                              console.log("error");
                            })
                      let promise=admin.database().ref("/users/"+uid+"/friend/"+oppnuid);
                      promise.set(true)
                         .then(function(){
                            console.log(uid+ "..."+ oppnuid+"..."+snapshot.val());
                          })
                          .catch(function(){
                            console.log("error");
                          })
                      let promise2=admin.database().ref("/users/"+oppnuid+"/friend/"+uid);
                      promise2.set(true)
                         .then(function(){
                            console.log(uid+ "..."+ oppnuid+"..."+snapshot.val());
                          })
                          .catch(function(){
                            console.log("error");
                          })
                        }
               });
exports.deleteHistoryRequested=functions.database.ref("/users/{uid}/deleteHistory/{oppnUid}/")
                .onCreate(async(snapshot, context)=>{
                  const uid=context.params.uid;
                  const oppnUid=context.params.oppnUid;
                  if(snapshot.val()===false){
                     //Get notification token of oppnUid
                     const myTokenP= admin.database().ref("/users/"+oppnUid+"/firebaseToken").once('value');
                     const myToken=await myTokenP.then(results=>{
                                  return results.val();
                     });
                     //Get name 
                     const uidNameP= admin.database().ref("/users/"+uid+"/name").once('value');
                     const uidName=await uidNameP.then(results=>{
                                  return results.val();
                     });
                     //Set Requestarrived in oppnUid
                     let setDeleteRequestInOppn=admin.database().ref("/users/"+oppnUid+"/deleteRequestArrived/"+uid)
                                                .set(false).then(function(){
                                                  console.log("successsss");
                                                }).catch(function(){
                                                  console.log("failureeee");
                                                });
                      const payload={
                        notification:{
                          title:"Request to delete history",
                          body: uidName+" wants to delete history"
                        }
                      }
                      const response= admin.messaging().sendToDevice(myToken, payload);
                  }
                });
exports.historydeleteRequestAccepted=functions.database.ref("/users/{uid}/deleteRequestArrived/{oppnUid}/")
                  .onUpdate(async (change, context)=>{
                         const uid=context.params.uid;
                         const oppnUid=context.params.oppnUid;
                         const beforeV=change.before.val();
                         const afterV=change.after.val();
                         if(beforeV===false && afterV===true)
                         {
                              let getTransactions =await admin.database().ref("/users/"+uid+"/myTransactions/"+oppnUid+"/transactions")
                                            .once('value').then(function(snapshot){
                                              console.log(snapshot.val());
                                              snapshot.forEach(element => {
                                                 let delConfirmedTransction= admin.database().ref("/confirmedTransactions/"+element.key)
                                                             .remove().then(function(){
                                                               console.log("transaction deleted"+element.key);
                                                             })
                                                             .catch(function(){
                                                                 console.log("problem");
                                                             });
                                              });
                                            })
                              
                              let deleteFromMe=admin.database().ref("/users/"+uid+"/myTransactions/"+oppnUid).remove();
                              let deleteFromOppn=admin.database().ref("/users/"+oppnUid+"/myTransactions/"+uid).remove();
                              const myTokenP= admin.database().ref("/users/"+oppnUid+"/firebaseToken").once('value');
                              const myToken=await myTokenP.then(results=>{
                                              return results.val();
                                          });
                              const uidNameP= admin.database().ref("/users/"+uid+"/name").once('value');
                                        const uidName=await uidNameP.then(results=>{
                                         return results.val();
                               });
                              const payload={
                                notification:{
                                  title:"History deletion request accepted",
                                  body: uidName+"accepted to delete transaction history with you"
                                }
                              }
                              const response= admin.messaging().sendToDevice(myToken, payload);
                              let deleteRequestArrivedDeletion=admin.database().ref("/users/"+uid+"/deleteRequestArrived/"+oppnUid).remove().then(function(){
                                console.log("deleteRequestArrivedDeletion success");
                              }).catch(function(){
                                console.log("deleteRequestArrivedDeletion failure");
                              });
                              let deleteHistoryDeletion=admin.database().ref("/users/"+oppnUid+"/deleteHistory/"+uid).remove().then(function(){
                                console.log("deleteHistoyDelte success");
                              }).catch(function(){
                                console.log("deleteHistoyDelte failure");
                              });

                         }
                  })
exports.historydeleteRequestRejected=functions.database.ref("/users/{uid}/deleteRequestArrived/{oppnUid}/")
                  .onDelete(async (snapshot, context)=>{
                  let uid=context.params.uid;
                  let oppnUid=context.params.oppnUid;
                  if(snapshot.val()===false)
                  {
                    let deleteHistoryDeletion=admin.database().ref("/users/"+oppnUid+"/deleteHistory/"+uid).remove().then(function(){
                      console.log("deleteHistoyDelte success");
                    }).catch(function(){
                      console.log("deleteHistoyDelte failure");
                    });
                    const myTokenP= admin.database().ref("/users/"+oppnUid+"/firebaseToken").once('value');
                    const myToken=await myTokenP.then(results=>{
                                              return results.val();
                                          });
                    const uidNameP= admin.database().ref("/users/"+uid+"/name").once('value');
                    const uidName=await uidNameP.then(results=>{
                                         return results.val();
                               });
                              const payload={
                                notification:{
                                  title:"History deletion request reject",
                                  body: uidName+" don't want to delete the transaction history with you"
                                }
                              }
                              const response= admin.messaging().sendToDevice(myToken, payload);
                  }
                  })