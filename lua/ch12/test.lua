t = { 10, 20, 30 }
for index, value in ipairs(t) do
    print(index,value)
end


t = { a = 10, b = 20, c = 30}
for key, value in pairs(t) do
    print(key,value)
end

t = { linkin = 10, fuck = 20, you = 30}
--最简单直接遍历所有键值对的方法
for k,v in next,t,nil do
    print(k,v)
end