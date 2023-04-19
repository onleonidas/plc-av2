import Control.Concurrent

main :: IO ()
main = do
  buffer1 <- newMVar 2000
  notify1 <- newEmptyMVar
  buffer2 <- newMVar 0
  notify2 <- newEmptyMVar
  buffer3 <- newMVar 0
  notify3 <- newEmptyMVar

  terminal <- newEmptyMVar
  putMVar terminal ()

  forkIO $ produce buffer1 notify1 terminal "Pepise-Cola"
  forkIO $ produce buffer2 notify2 terminal "Guaraná Polo Norte"
  forkIO $ produce buffer3 notify3 terminal "Guarana Quate"

  forkIO $ consume buffer1 notify1 terminal "Lucas" "Pepise-Cola"
  forkIO $ consume buffer2 notify2 terminal "Mariana" "Guaraná Polo Norte"
  forkIO $ consume buffer3 notify3 terminal "Pedro" "Guarana Quate"
  forkIO $ consume buffer1 notify1 terminal "João" "Pepise-Cola"

  threadDelay 1 -- wait for 10 seconds before terminating the program

produce :: MVar Int -> MVar () -> MVar () -> String -> IO ()
produce buffer notify terminal name = loop
  where 
    loop = do
      val1 <- takeMVar buffer
      if val1 < 1000 then do
        takeMVar terminal
        putStrLn $ "O refrigerante " ++ name ++ " foi reabastecido com 1000 ml, e agora possui " ++ show (val1+1000)  ++" ml"
        threadDelay 1500000 -- wait for 1 second
        putMVar terminal ()
        putMVar buffer (val1+1000)
        putMVar notify () 
        loop 
      else do
        putMVar buffer val1
        putMVar notify () 
        loop 
    

consume :: MVar Int -> MVar () -> MVar () -> String -> String -> IO ()
consume buffer notify terminal name refri = loop
  where 
    loop = do
      val <- takeMVar buffer
      if val >= 300 then do
        takeMVar terminal
        putStrLn $ "O cliente " ++ name ++ " do refrigerante " ++ refri ++ " esta enchendo seu copo"
        threadDelay 1000000 -- wait for 1 second
        putMVar terminal ()
        putMVar buffer (val-300)
        takeMVar notify 
        loop 
      else do
        putMVar buffer val
        takeMVar notify 
        loop
