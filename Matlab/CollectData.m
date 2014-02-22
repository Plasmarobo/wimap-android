function [m, average_data] = CollectData(data)
m = size(data,2);
n = size(data,3);
average_data=zeros(m,1,1);
for i = 1:m
    for j = 1:n
        average_data(i) = average_data(i) + data(1,i,j);
    end
    average_data(i) = average_data(i) / n;
end
end