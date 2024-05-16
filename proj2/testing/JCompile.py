import os
import subprocess

def compile_java_files():
    # 获取当前脚本所在目录
    current_dir = os.path.dirname(os.path.abspath(__file__))
    # 获取父目录（proj2目录）
    parent_dir = os.path.dirname(current_dir)
    
    # 构建fileList文件的路径
    file_list_path = os.path.join(parent_dir, 'fileList')
    
    # 检查fileList文件是否存在
    if not os.path.isfile(file_list_path):
        print(f"fileList file not found: {file_list_path}")
        return
    
    # 更改工作目录到父目录
    os.chdir(parent_dir)
    
    # 执行javac命令
    command = ['javac', '@fileList']
    try:
        result = subprocess.run(command, check=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
        print(result.stdout)
        print(result.stderr)
    except subprocess.CalledProcessError as e:
        print(f"Error occurred during compilation: {e.stderr}")

if __name__ == "__main__":
    compile_java_files()
