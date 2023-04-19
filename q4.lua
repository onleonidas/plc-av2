-- define shared integer values
local hp_pikachu = 800
local hp_raichu = 1000
local ended = false 

-- define Pikachu coroutine
local function Pikachu()
  while hp_pikachu > 0 and hp_raichu > 0 do
    -- generate random attack value
    local num = math.random(1, 20)
    local atk = 0
    if num <= 10 then
      atk = 50
    elseif num <= 15 then
      atk = 100
    elseif num <= 18 then
      atk = 150
    else
      atk = 200
    end
    
    -- apply attack to Raichu's HP
    hp_raichu = hp_raichu - atk
    print("Pikachu attacked Raichu for "..atk.." damage!")
    print("Raichu's HP: "..hp_raichu)
    print()
    
    
    -- yield coroutine to allow Raichu to attack
    coroutine.yield()
  end
  ended = true
end

-- define Raichu coroutine
local function Raichu()
  while hp_pikachu > 0 and hp_raichu > 0 do
    -- generate random attack value
    local num = math.random(1, 20)
    local atk = 0
    if num <= 10 then
      atk = 50
    elseif num <= 15 then
      atk = 100
    elseif num <= 18 then
      atk = 150
    else
      atk = 200
    end
    
    -- apply attack to Pikachu's HP
    hp_pikachu = hp_pikachu - atk
    print("Raichu attacked Pikachu for "..atk.." damage!")
    print("Pikachu's HP: "..hp_pikachu)
    
    -- yield coroutine to allow Pikachu to attack
    coroutine.yield()
  end
  ended = true
end

-- create Pikachu coroutine
local co_pikachu = coroutine.create(Pikachu)

-- create Raichu coroutine
local co_raichu = coroutine.create(Raichu)

-- start the battle
while hp_pikachu > 0 and hp_raichu > 0 do 
  
  -- resume Raichu coroutine
  coroutine.resume(co_raichu)
  -- resume Pikachu coroutine
  coroutine.resume(co_pikachu)
  
end
if hp_pikachu < 0 then
  print("Pikachu perdeu")
else 
  print("Raichu perdeu")
end
print("Battle is over!")
