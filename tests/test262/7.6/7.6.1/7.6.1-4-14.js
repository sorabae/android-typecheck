// TODO getter/setter
//   function testcase() 
//   {
//     var test0 = 0, test1 = 1, test2 = 2;
//     var tokenCodes = {
//       set public(value) 
//       {
//         test0 = value;
//       },
//       get public()
//       {
//         return test0;
//       },
//       set yield(value) 
//       {
//         test1 = value;
//       },
//       get yield()
//       {
//         return test1;
//       },
//       set interface(value) 
//       {
//         test2 = value;
//       },
//       get interface()
//       {
//         return test2;
//       }
//     };
//     var arr = ['public', 'yield', 'interface', ];
//     for(var p in tokenCodes)
//     {
//       for(var p1 in arr)
//       {
//         if (arr[p1] === p)
//         {
//           if (! tokenCodes.hasOwnProperty(arr[p1]))
//           {
//             return false;
//           }
//           ;
//         }
//       }
//     }
//     return true;
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
