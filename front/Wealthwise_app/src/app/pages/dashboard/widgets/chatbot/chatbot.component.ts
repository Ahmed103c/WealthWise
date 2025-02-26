import { Component } from '@angular/core';
import { AuthService } from '../../../../services/auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-chatbot',
  imports: [FormsModule, CommonModule],
  templateUrl: './chatbot.component.html',
  styleUrl: './chatbot.component.scss',
})
export class ChatbotComponent {
  constructor(private authservice: AuthService) {}

  question: string = '';
  history: { question: string; response: string }[] = [];
  isloading: boolean = true;
  onWelcome: boolean = true;

  sendQuestion() {
    this.onWelcome = false;
    const newMessage = { question: this.question, response: '' };
    this.history.push(newMessage);

    const messageIndex = this.history.length - 1;

    this.authservice.getAnswer(this.question).subscribe(
      (data) => {
        this.isloading = false;
        this.history[messageIndex].response = data.response;
      },
      (error) => {
        console.error('Erreur', error);
        this.history[messageIndex].response = 'Une erreur est survenue.';
      }
    );
    this.question = '';
    this.isloading = true;
  }
}
