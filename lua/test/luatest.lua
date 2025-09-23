local t = {}
t[1] = "a"
t[2] = "b"
t[3] = "c"
t[4] = "d"
t[5] = "e"
t[6] = "f"
t[7] = "g"
t["linkin"] = "test"
t[2] = nil
t[7] = nil

print(#t)       -- Lua的长度操作符
print(t[1])
print(t[2])
print(t[3])
print(t[4])
print(t[5])
print(t[6])
print(t[7])
print(t["linkin"])