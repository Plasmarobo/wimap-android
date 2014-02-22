function [filtered_curve] = DistanceFilter(distance_array, sample_count)
    G = 2;
    filtered_curve = zeros(length(distance_array), 1,1);
    for i = 1:length(distance_array)
        for j = (i-sample_count):i
            if (j < 1)
                filtered_curve(i) = distance_array(i).*sample_count;
                break;
            end
            filtered_curve(i) = filtered_curve(i) + distance_array(j)/power(G, -(i-j));
        end
    end
    filtered_curve = filtered_curve./(power(G, sample_count+1)-1);
end